package org.hackystat.dailyprojectdata.resource.coverage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

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

  /** Setup the CoverageData that is tested. */
  @Before
  public void setUp() {
    // First, create the test SensorData instances.
    XMLGregorianCalendar runtimeCalendar = Tstamp.makeTimestamp(new Date().getTime());
    String runtimeString = runtimeCalendar.toString();
    SensorData sensorDataAusten01 = TestCoverageData.createData(runtimeString, runtimeString,
        userAusten, "C:\\foo.java");
    SensorData sensorDataAusten02 = TestCoverageData.createData(runtimeString, runtimeString,
        userAusten, "C:\\foo2.java");
    SensorData sensorDataJulie01 = TestCoverageData.createData(runtimeString, runtimeString,
        userJulie, "C:\\foo3.java");
    SensorData sensorDataJulie02 = TestCoverageData.createData(runtimeString, runtimeString,
        userJulie, "C:\\foo4.java");
    SensorData sensorDataAaron01 = TestCoverageData.createData(runtimeString, runtimeString,
        userAaron, "C:\\foo5.java");

    // Second, adds the sensor.
    this.data.addCoverageData(sensorDataAusten01);
    this.data.addCoverageData(sensorDataAusten02);
    this.data.addCoverageData(sensorDataJulie01);
    this.data.addCoverageData(sensorDataJulie02);
    this.data.addCoverageData(sensorDataAaron01);

    // Finally, create the wrapped CoverageData instances.
    this.dataAusten01 = new CoverageData(sensorDataAusten01);
    this.dataAusten02 = new CoverageData(sensorDataAusten02);
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
    List<CoverageData> austenData = this.data.getData(this.userAusten);
    assertEquals("The amount of data returned for " + userAusten + " is incorrect.", 2,
        austenData.size());
    assertTrue("The returned data does not have the first data set.", austenData
        .contains(this.dataAusten01));
    assertTrue("The returned data does not have the second data set.", austenData
        .contains(this.dataAusten02));

    // Tests a non-existent user.
    List<CoverageData> emptyData = this.data.getData("testUser");
    assertEquals("There should be no data for a non-existent user.", 0, emptyData.size());
  }
}
