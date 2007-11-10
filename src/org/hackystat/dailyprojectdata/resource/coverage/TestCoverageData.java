package org.hackystat.dailyprojectdata.resource.coverage;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the CoverageData wraps a SensorData instance as intended.
 * @author aito
 * 
 */
public class TestCoverageData {
  /** The data wrapper class that is tested. */
  private CoverageData data = null;

  /** Setup this test class. */
  @Before
  public void setUp() {
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(new Date().getTime());
    this.data = new CoverageData(this.createData(runtime, "austen@hawaii.edu", "C:\\foo.java",
        "line", 10.0, 1.0));
  }

  /** Tests if the correct amount of uncovered coverage entities is returned. */
  @Test
  public void testGetUncovered() {
    assertEquals("The amount of uncovered lines is incorrect.", 10, this.data.getUncovered());
  }

  /** Tests if the correct amount of uncovered coverage entities is returned. */
  @Test
  public void testGetCovered() {
    assertEquals("The amount of covered lines is incorrect.", 1, this.data.getCovered());
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
