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
 * Tests if the CoverageCounter class manages the timestamp -> data instance
 * mappings correctly.
 * @author aito
 * 
 */
public class TestCoverageCounter {
  /** The fields that are tested. */
  private CoverageCounter counter = new CoverageCounter();
  private CoverageData secondBatch01 = null;
  private CoverageData secondBatch02 = null;
  private static final String GRANULARITY_LINE = "line";

  /** Setup each test case with new instances. */
  @Before
  public void setUp() {
    // First, create the SensorData instances.
    XMLGregorianCalendar firstRuntime = Tstamp.makeTimestamp(new Date().getTime());
    SensorData sensorFirstBatch01 = this.createData(firstRuntime, "C:\\StringListCodec.java",
        GRANULARITY_LINE, 4.0, 0.0);
    SensorData sensorFirstBatch02 = this.createData(firstRuntime, "C:\\XmlDataSensor.java",
        GRANULARITY_LINE, 5.0, 0.0);
    XMLGregorianCalendar secondRuntime = Tstamp.makeTimestamp(new Date().getTime() + 10);
    SensorData sensorSecondBatch01 = this.createData(secondRuntime, "C:\\PropertyMap.java",
        GRANULARITY_LINE, 10.0, 0.0);
    SensorData sensorSecondBatch02 = this.createData(secondRuntime, "C:\\Option.java",
        GRANULARITY_LINE, 5.0, 1.0);

    // Then, add them to the counter class.
    this.counter.addCoverageData(sensorFirstBatch01);
    this.counter.addCoverageData(sensorFirstBatch02);
    this.counter.addCoverageData(sensorSecondBatch01);
    this.counter.addCoverageData(sensorSecondBatch02);

    // Finally, create the CoverageData instances that will be tested.
    this.secondBatch01 = new CoverageData(sensorSecondBatch01);
    this.secondBatch02 = new CoverageData(sensorSecondBatch02);
  }

  /** Tests if the latest batch of data is returned. */
  @Test
  public void testGetLatestBatch() {
    CoverageDataContainer latestData = this.counter.getLatestBatch();
    assertEquals("The latest data count is incorrect.", 2, latestData.getData().size());
    assertTrue("The second batch does not have the first entry.", latestData.getData()
        .contains(this.secondBatch01));
    assertTrue("The second batch does not have the second entry.", latestData.getData()
        .contains(this.secondBatch02));
  }

  /** Tests if null is returned when there is no latest batch of data. */
  @Test
  public void testGetLatestBatchNoData() {
    CoverageCounter counter = new CoverageCounter();
    assertNull("No latest batch does not return null.", counter.getLatestBatch());
  }

  /** Tests if the counter has data or not. */
  @Test
  public void testHasData() {
    // Tests if there is no data.
    CoverageCounter counter = new CoverageCounter();
    assertFalse("There should be no data.", counter.hasData());

    // Tests if data exists.
    assertTrue("There should be existing data.", this.counter.hasData());
  }

  /**
   * A helper method used to create the SensorData instances used to by this
   * test class.
   * @param runtime the runtime of the SensorData instance.
   * @param resource the specified resource.
   * @param granularity the specified granularity.
   * @param uncovered the specified amount of uncovered entity types.
   * @param covered the specified amount of covered entity types.
   * @return the populated SensorData instance.
   */
  private SensorData createData(XMLGregorianCalendar runtime, String resource,
      String granularity, double uncovered, double covered) {
    SensorData data = new SensorData();
    data.setOwner("austen@hawaii.edu");
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
