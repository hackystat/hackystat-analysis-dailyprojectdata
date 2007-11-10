package org.hackystat.dailyprojectdata.resource.coverage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the CoverageData class performs operations on the wrapped SensorData
 * instances as intended.
 * @author aito
 * 
 */
public class TestCoverageDataContainer {
  /** The fields used to test the CoverageData instance. */
  private CoverageDataContainer data = new CoverageDataContainer();
  private String userAusten = "austen@hawaii.edu";
  private String userJulie = "jsakuda@hawaii.edu";
  private String userAaron = "kagawaa@hawaii.edu";
  private CoverageData dataAusten01 = null;
  private CoverageData dataAusten02 = null;
  private CoverageData dataJulie02 = null;
  private static final String GRANULARITY_LINE = "line";

  /** Setup the CoverageData that is tested. */
  @Before
  public void setUp() {
    // First, create the test SensorData instances.
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(new Date().getTime());
    SensorData sensorDataAusten01 = this.createData(runtime, userAusten, "C:\\foo.java",
        GRANULARITY_LINE, 10.0, 1.0);
    SensorData sensorDataAusten02 = this.createData(runtime, userAusten, "C:\\foo2.java",
        GRANULARITY_LINE, 10.0, 1.0);
    SensorData sensorDataJulie01 = this.createData(runtime, userJulie, "C:\\foo3.java",
        GRANULARITY_LINE, 10.0, 1.0);
    SensorData sensorDataJulie02 = this.createData(runtime, userJulie, "C:\\foo4.java",
        "class", 10.0, 1.0);
    SensorData sensorDataAaron01 = this.createData(runtime, userAaron, "C:\\foo5.java",
        "method", 10.0, 1.0);

    // Second, adds the sensor.
    this.data.addCoverageData(sensorDataAusten01);
    this.data.addCoverageData(sensorDataAusten02);
    this.data.addCoverageData(sensorDataJulie01);
    this.data.addCoverageData(sensorDataJulie02);
    this.data.addCoverageData(sensorDataAaron01);

    // Finally, create the wrapped CoverageData instances.
    this.dataAusten01 = new CoverageData(sensorDataAusten01);
    this.dataAusten02 = new CoverageData(sensorDataAusten02);
    this.dataJulie02 = new CoverageData(sensorDataJulie02);
  }

  /** Tests if the correct owners of the wrapped SensorData are returned. */
  @Test
  public void testGetOwners() {
    List<String> owners = this.data.getOwners();
    assertEquals("The amount of owners is incorrect.", 3, owners.size());
    assertTrue("The owner " + userAusten + " does not exist.", owners
        .contains("austen@hawaii.edu"));
    assertTrue("The owner " + userJulie + " does not exist.", owners
        .contains("jsakuda@hawaii.edu"));
    assertTrue("The owner " + userAaron + " does not exist.", owners
        .contains("kagawaa@hawaii.edu"));
  }

  /**
   * Tests if the correct data is returned using the specified user and
   * granularity.
   */
  @Test
  public void testGetData() {
    // Tests if the one owner with multiple data sets is returned correctly.
    List<CoverageData> austenData = this.data.getData(this.userAusten, "line");
    assertEquals("The amount of data returned for " + userAusten + " is incorrect.", 2,
        austenData.size());
    assertTrue("The returned data does not have the first data set.", austenData
        .contains(this.dataAusten01));
    assertTrue("The returned data does not have the second data set.", austenData
        .contains(this.dataAusten02));

    // Tests if data is returned when a user has multiple data sets with
    // different granularity.
    List<CoverageData> julieData = this.data.getData(this.userJulie, "class");
    assertEquals("The amount of data returned for " + userJulie + " is incorrect.", 1,
        julieData.size());
    assertTrue("The returned data does not have the first data set.", julieData
        .contains(this.dataJulie02));

    // Tests a non-existent user.
    List<CoverageData> emptyData = this.data.getData("testUser", "line");
    assertEquals("There should be no data for a non-existent user.", 0, emptyData.size());

    // Tests non-existent granularity.
    emptyData = this.data.getData(this.userAaron, "testGranularity");
    assertEquals("There should be no data for a non-existent granularity.", 0, emptyData
        .size());
  }

  /**
   * A helper method used to create the SensorData instances used to by this
   * test class.
   * @param runtime the runtime of the SensorData instance.
   * @param owner the specified owner.
   * @param resource the specified resource.
   * @param granularity the specified granularity.
   * @param uncovered the specified amount of uncovered entity types.
   * @param covered the specified amount of covered entity types.
   * @return the populated SensorData instance.
   */
  private SensorData createData(XMLGregorianCalendar runtime, String owner, String resource,
      String granularity, double uncovered, double covered) {
    SensorData data = new SensorData();
    data.setOwner(owner);
    data.setRuntime(runtime);
    data.setTool("Emma");
    data.setResource(resource);

    Properties props = new Properties();
    Property typeProperty = new Property();
    typeProperty.setKey("Granularity");
    typeProperty.setValue(granularity);
    props.getProperty().add(typeProperty);

    Property uncoveredProperty = new Property();
    uncoveredProperty.setKey("Uncovered");
    uncoveredProperty.setValue(Double.valueOf(uncovered).toString());
    props.getProperty().add(uncoveredProperty);

    Property coveredProperty = new Property();
    coveredProperty.setKey("Covered");
    coveredProperty.setValue(Double.valueOf(covered).toString());
    props.getProperty().add(coveredProperty);

    data.setProperties(props);
    return data;
  }
}
