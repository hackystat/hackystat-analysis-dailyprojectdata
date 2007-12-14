package org.hackystat.dailyprojectdata.resource.commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.resource.coverage.CoverageData;
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
public class TestCommitData {
  /** The fields that are tested in this test class. */
  private CommitData commitData = null;
  private SensorData sensorData = null;

  /** Setup this test class. */
  @Before
  public void setUp() {
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(new Date().getTime());
    this.sensorData = createData(runtime.toString(), runtime.toString(), "austen@hawaii.edu",
        "C:\\foo.java", "4", "10", "255");
    this.commitData = new CommitData(this.sensorData);
  }

  /** Tests if the commit data returns the correct amount of lines added. */
  @Test
  public void testGetLinesAdded() {
    assertEquals("The amount of lines added is incorrect.", 4, this.commitData
        .getLinesAdded());
  }

  /** Tests if the commit data returns the correct amount of lines deleted. */
  @Test
  public void testGetLinesDeleted() {
    assertEquals("The amount of lines deleted is incorrect.", 10, this.commitData
        .getLinesDeleted());
  }

  /** Tests if the commit data returns the correct amount of lines changed. */
  @Test
  public void testGetLinesChanged() {
    assertEquals("The amount of lines changed is incorrect.", 255, this.commitData
        .getLinesChanged());
  }

  /** Tests the overriden .equals method returns the correct values. */
  @Test
  public void testEquals() {
    // First, test equal instances.
    CommitData newCoverageData = new CommitData(this.sensorData);
    assertTrue("Instances with the same SensorData are not equal.", this.commitData
        .equals(newCoverageData));

    // Then, test if the same instance returns true.
    assertTrue("The same instances are equal.", this.commitData.equals(this.commitData));

    // Next, test instances with different SensorData objects.
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(new Date().getTime() + 10);
    SensorData sensorData = createData(runtime.toString(), runtime.toString(),
        "austen@hawaii.edu", "C:\\foo.java", "1", "2", "100");
    assertFalse("Instances with the differnt SensorData are not equal.", this.commitData
        .equals(new CoverageData(sensorData)));

    // Finally, test if different object types are not equal.
    assertFalse("Instances with the different SensorData are not equal.", this.commitData
        .equals("Foo String"));
  }

  /**
   * A helper method used to create the SensorData instances used to by this
   * test class.
   * @param timestamp the timestamp of the created sensor data instance.
   * @param runtime the runtime of the SensorData instance.
   * @param owner the specified owner.
   * @param resource the specified resource.
   * @param linesAdded the total lines added in returned data instance.
   * @param linesDeleted the total lines deleted in returned data instance.
   * @param linesChanged the total lines changed in returned data instance.
   * @return the populated SensorData instance.
   */
  public static SensorData createData(String timestamp, String runtime, String owner,
      String resource, String linesAdded, String linesDeleted, String linesChanged) {
    try {
      SensorData data = new SensorData();
      data.setOwner(owner);
      data.setTimestamp(Tstamp.makeTimestamp(timestamp));
      data.setRuntime(Tstamp.makeTimestamp(runtime));
      data.setSensorDataType("Commit");
      data.setTool("Subversion");
      data.setResource(resource);

      // Sets the lines added property.
      Properties props = new Properties();
      Property linesAddedProperty = new Property();
      linesAddedProperty.setKey("linesAdded");
      linesAddedProperty.setValue(linesAdded);
      props.getProperty().add(linesAddedProperty);

      // Sets the lines removed property.
      Property linesDeletedProperty = new Property();
      linesDeletedProperty.setKey("linesDeleted");
      linesDeletedProperty.setValue(linesDeleted);
      props.getProperty().add(linesDeletedProperty);

      // Sets the lines changed property.
      Property linesChangedProperty = new Property();
      linesChangedProperty.setKey("totalLines");
      linesChangedProperty.setValue(linesChanged);
      props.getProperty().add(linesChangedProperty);

      data.setProperties(props);
      return data;
    }
    catch (Exception e) {
      fail("Failed to create test data. " + e.getMessage());
    }
    return null;
  }
}
