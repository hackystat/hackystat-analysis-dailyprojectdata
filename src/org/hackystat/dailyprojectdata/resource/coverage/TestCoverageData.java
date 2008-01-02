package org.hackystat.dailyprojectdata.resource.coverage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
 * 
 * @author aito
 * 
 */
public class TestCoverageData {
  /** The fields that are tested in this test class. */
  private CoverageData coverageData = null;
  private SensorData sensorData = null;
  private XMLGregorianCalendar runtime = null;

  /**
   * Setup this test class.
   * 
   * @throws Exception If problems occur.
   */
  @Before
  public void setUp() throws Exception {
    this.runtime = Tstamp.makeTimestamp(new Date().getTime());
    this.sensorData = createData(this.runtime.toString(), this.runtime.toString(),
        "austen@hawaii.edu", "C:\\foo.java");
    this.coverageData = new CoverageData(this.sensorData);
  }

  /** Tests if the correct runtime is returned. */
  @Test
  public void testGetRuntime() {
    assertEquals("Returned runtime is incorrect.", this.runtime, this.coverageData.getRuntime());
  }

  /** Tests if the correct amount of uncovered coverage entities is returned. */
  @Test
  public void testGetUncovered() {
    assertEquals("The amount of 'line' uncovered data is incorrect.", 1, this.coverageData
        .getUncovered(CoverageData.GRANULARITY_LINE));
    assertEquals("The amount of 'method' uncovered data is incorrect.", 2, this.coverageData
        .getUncovered(CoverageData.GRANULARITY_METHOD));
    assertEquals("The amount of 'class' uncovered data is incorrect.", 3, this.coverageData
        .getUncovered(CoverageData.GRANULARITY_CLASS));
    assertEquals("The amount of 'block' uncovered data is incorrect.", 4, this.coverageData
        .getUncovered(CoverageData.GRANULARITY_BLOCK));
  }

  /** Tests if the correct amount of uncovered coverage entities is returned. */
  @Test
  public void testGetCovered() {
    assertEquals("The amount of 'line' covered data is incorrect.", 5, this.coverageData
        .getCovered(CoverageData.GRANULARITY_LINE));
    assertEquals("The amount of 'method' covered data is incorrect.", 6, this.coverageData
        .getCovered(CoverageData.GRANULARITY_METHOD));
    assertEquals("The amount of 'class' covered data is incorrect.", 7, this.coverageData
        .getCovered(CoverageData.GRANULARITY_CLASS));
    assertEquals("The amount of 'block' covered data is incorrect.", 8, this.coverageData
        .getCovered(CoverageData.GRANULARITY_BLOCK));
  }

  /**
   * Tests if the resource returned from the wrapper class is the same as the resource in the
   * SensorData instance.
   */
  @Test
  public void testGetResource() {
    assertEquals("The returned resource is incorrect.", this.sensorData.getResource(),
        this.coverageData.getResource());
  }

  /**
   * Tests if the correct Property is returned or that null is returned if the property does not
   * exist.
   */
  @Test
  public void testGetCoverageProperty() {
    // First, let's test an existing property.
    String propertyName = CoverageData.GRANULARITY_LINE + "_Covered";
    Property coveredProperty = this.coverageData.getCoverageProperty(propertyName);
    assertEquals("The Covered Property Name is incorrect.", propertyName, coveredProperty.getKey());
    assertEquals("The Covered Property Value is incorrect.", "5", coveredProperty.getValue());

    // Next, let's test if a non-existent property returns null.
    assertNull("Null was not returned for a non-existent property.", this.coverageData
        .getCoverageProperty("Foo Property"));
  }

  /**
   * Tests the overridden .equals method returns the correct values.
   * 
   * @throws Exception if problems occur
   */
  @Test
  public void testEquals() throws Exception {
    // First, test equal instances.
    CoverageData newCoverageData = new CoverageData(this.sensorData);
    assertTrue("Instances with the same SensorData are equal.", this.coverageData
        .equals(newCoverageData));

    // Then, test if the same instance returns true.
    assertTrue("The same instances are equal.", this.coverageData.equals(this.coverageData));

    // Next, test instances with different SensorData objects.
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(new Date().getTime() + 10);
    SensorData sensorData = createData(runtime.toString(), runtime.toString(), "austen@hawaii.edu",
        "C:\\foo.java");
    assertFalse("Instances with the differnt SensorData are not equal.", this.coverageData
        .equals(new CoverageData(sensorData)));

    // Finally, test if different object types are not equal.
    assertFalse("Instances with the different SensorData are not equal.", this.coverageData
        .equals("Foo String"));
  }

  /**
   * Tests if the correct coverage data is returned regardless of the granularity's case.
   */
  @Test
  public void testGranularityCaseInsensitivity() {
    // Tests the covered data case insensitivity.
    assertEquals("Incorrect number of covered lines using 'Line'.", 5, this.coverageData
        .getCovered("Line"));
    assertEquals("Incorrect number of covered lines using 'LINE'.", 5, this.coverageData
        .getCovered("LINE"));
    assertEquals("Incorrect number of covered lines using 'lINE'.", 5, this.coverageData
        .getCovered("lINE"));
    assertEquals("Incorrect number of covered lines using 'line'.", 5, this.coverageData
        .getCovered("line"));

    // Tests the uncovered data case insensitivity.
    assertEquals("Incorrect number of uncovered lines using 'Line'.", 1, this.coverageData
        .getUncovered("Line"));
    assertEquals("Incorrect number of uncovered lines using 'LINE'.", 1, this.coverageData
        .getUncovered("LINE"));
    assertEquals("Incorrect number of uncovered lines using 'lINE'.", 1, this.coverageData
        .getUncovered("lINE"));
    assertEquals("Incorrect number of uncovered lines using 'line'.", 1, this.coverageData
        .getUncovered("line"));

  }

  /**
   * A helper method used to create the SensorData instances used to by this test class.
   * 
   * @param timestamp the timestamp of the created sensor data instance.
   * @param runtime the runtime of the SensorData instance.
   * @param owner the specified owner.
   * @param resource the specified resource.
   * @return the populated SensorData instance.
   * @throws Exception if problems occur.
   */
  public static SensorData createData(String timestamp, String runtime, String owner,
      String resource) throws Exception {
    SensorData data = new SensorData();
    data.setOwner(owner);
    data.setTimestamp(Tstamp.makeTimestamp(timestamp));
    data.setRuntime(Tstamp.makeTimestamp(runtime));
    data.setSensorDataType("Coverage");
    data.setTool("Emma");
    data.setResource(resource);

    // Sets the uncovered values.
    Properties props = new Properties();
    Property lineUncoveredProperty = new Property();
    lineUncoveredProperty.setKey("line_Uncovered");
    lineUncoveredProperty.setValue("1");
    props.getProperty().add(lineUncoveredProperty);

    Property methodUncoveredProperty = new Property();
    methodUncoveredProperty.setKey("method_Uncovered");
    methodUncoveredProperty.setValue("2");
    props.getProperty().add(methodUncoveredProperty);

    Property classUncoveredProperty = new Property();
    classUncoveredProperty.setKey("class_Uncovered");
    classUncoveredProperty.setValue("3");
    props.getProperty().add(classUncoveredProperty);

    Property blockUncoveredProperty = new Property();
    blockUncoveredProperty.setKey("block_Uncovered");
    blockUncoveredProperty.setValue("4");
    props.getProperty().add(blockUncoveredProperty);

    // Sets the covered values.
    Property lineCoveredProperty = new Property();
    lineCoveredProperty.setKey("line_Covered");
    lineCoveredProperty.setValue("5");
    props.getProperty().add(lineCoveredProperty);

    Property methodCoveredProperty = new Property();
    methodCoveredProperty.setKey("method_Covered");
    methodCoveredProperty.setValue("6");
    props.getProperty().add(methodCoveredProperty);

    Property classCoveredProperty = new Property();
    classCoveredProperty.setKey("class_Covered");
    classCoveredProperty.setValue("7");
    props.getProperty().add(classCoveredProperty);

    Property blockCoveredProperty = new Property();
    blockCoveredProperty.setKey("block_Covered");
    blockCoveredProperty.setValue("8");
    props.getProperty().add(blockCoveredProperty);

    data.setProperties(props);
    return data;
  }
}
