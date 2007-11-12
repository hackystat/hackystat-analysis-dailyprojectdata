package org.hackystat.dailyprojectdata.resource.coverage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

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
  /** The fields that are tested in this test class. */
  private CoverageData coverageData = null;
  private SensorData sensorData = null;

  /** Setup this test class. */
  @Before
  public void setUp() {
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(new Date().getTime());
    this.sensorData = this.createData(runtime, "austen@hawaii.edu", "C:\\foo.java", "line",
        10.0, 1.0);
    this.coverageData = new CoverageData(this.sensorData);
  }

  /** Tests if the correct amount of uncovered coverage entities is returned. */
  @Test
  public void testGetUncovered() {
    assertEquals("The amount of uncovered lines is incorrect.", 10, this.coverageData
        .getUncovered());
  }

  /** Tests if the correct amount of uncovered coverage entities is returned. */
  @Test
  public void testGetCovered() {
    assertEquals("The amount of covered lines is incorrect.", 1, this.coverageData
        .getCovered());
  }

  /**
   * Tests if the resource returned from the wrapper class is the same as the
   * resource in the SensorData instance.
   */
  @Test
  public void testGetResource() {
    assertEquals("The returned resource is incorrect.", this.sensorData.getResource(),
        this.coverageData.getResource());
  }

  /**
   * Tests if the correct Property is returned or that null is returned if the
   * property does not exist.
   */
  @Test
  public void testGetCoverageProperty() {
    // First, let's test an existing property.
    Property coveredProperty = this.coverageData.getCoverageProperty("Covered");
    assertEquals("The Covered Property Name is incorrect.", "Covered", coveredProperty
        .getKey());
    assertEquals("The Covered Property Value is incorrect.", "1.0", coveredProperty.getValue());

    // Next, let's test if a non-existent property returns null.
    assertNull("Null was not returned for a non-existent property.", this.coverageData
        .getCoverageProperty("Foo Property"));
  }

  /** Tests the overriden .equals method returns the correct values. */
  @Test
  public void testEquals() {
    // First, test equal instances.
    CoverageData newCoverageData = new CoverageData(this.sensorData);
    assertTrue("Instances with the same SensorData are equal.", this.coverageData
        .equals(newCoverageData));

    // Then, test if the same instance returns true.
    assertTrue("The same instances are equal.", this.coverageData.equals(this.coverageData));

    // Next, test instances with different SensorData objects.
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(new Date().getTime() + 10);
    SensorData sensorData = this.createData(runtime, "austen@hawaii.edu", "C:\\foo.java",
        "line", 10.0, 1.0);
    assertFalse("Instances with the differnt SensorData are not equal.", this.coverageData
        .equals(new CoverageData(sensorData)));

    // Finally, test if different object types are not equal.
    assertFalse("Instances with the differnt SensorData are not equal.", this.coverageData
        .equals("Foo String"));

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
