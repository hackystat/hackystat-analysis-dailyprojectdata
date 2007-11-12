package org.hackystat.dailyprojectdata.resource.coverage;

import static org.junit.Assert.assertSame;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.coverage.jaxb.CoverageDailyProjectData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the Coverage portion of the DailyProjectData REST API.
 * 
 * @author jsakuda
 */
public class TestCoverageRestApi extends DailyProjectDataTestHelper {
  /** Constant for Project. */
  private static final String PROJECT = "Default";

  /** The name of the coverage tool. */
  private static final String TOOL_NAME = "Emma";

  /** The user for this test case. */
  private String user = "TestCodeIssue@hackystat.org";

  /**
   * Test that GET {host}/codeissue/{user}/default/{starttime} works properly.
   * First, it creates a test user and sends some sample CodeIssue data to the
   * SensorBase. Then, it invokes the GET request and checks to see that it
   * obtains the right answer. Finally, it deletes the data and the user.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testGetCoverage() throws Exception {
    // First, create a batch of Coverage sensor data.
    String runtime = "2007-10-30T02:00:00";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(
        makeCoverage("2007-10-30T02:00:00", user, runtime, "file://Foo.java", TOOL_NAME,
            "Line", "10.0", "15.0"));
    batchData.getSensorData().add(
        makeCoverage("2007-10-30T02:10:00", user, runtime, "file://Foo.java", TOOL_NAME,
            "Class", "10.0", "15.0"));
    batchData.getSensorData().add(
        makeCoverage("2007-10-30T02:15:00", user, runtime, "file://Foo2.java", TOOL_NAME,
            "Line", "5.0", "1.0"));
    batchData.getSensorData().add(
        makeCoverage("2007-10-30T02:20:00", user, runtime, "file://Foo.java", TOOL_NAME,
            "Method", "11.0", "25.0"));

    // Connect to the sensorbase and register the DailyProjectDataCodeIssue
    // user.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
    client.authenticate();
    // Send the sensor data to the SensorBase.
    client.putSensorDataBatch(batchData);

    // Now connect to the DPD server.
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(
        getDailyProjectDataHostName(), user, user);
    dpdClient.authenticate();

    XMLGregorianCalendar requestTstamp = Tstamp.makeTimestamp("2007-10-30");
    CoverageDailyProjectData lineCoverage = dpdClient.getCoverage(user, PROJECT,
        requestTstamp, "Line");
    assertSame("Checking for 2 'Line' coverage entries.", 2, lineCoverage.getConstructData()
        .size());

    CoverageDailyProjectData classCoverage = dpdClient.getCoverage(user, PROJECT,
        requestTstamp, "Class");
    assertSame("Checking for 1 'Class' coverage entries.", 1, classCoverage.getConstructData()
        .size());

    // Then, delete all sensor data sent by this user.
    SensorDataIndex index = client.getSensorDataIndex(user);
    for (SensorDataRef ref : index.getSensorDataRef()) {
      client.deleteSensorData(user, ref.getTimestamp());
    }
    // Now delete the user too.
    client.deleteUser(user);
  }

  /**
   * Creates a sample SensorData Coverage instance.
   * 
   * @param tstampString The timestamp as a string.
   * @param user The user.
   * @param runtimeString The runtime.
   * @param resource the resource of the sensor data.
   * @param tool The tool.
   * @param granularity The granularity of the coverage data (Line, Class,
   * Method, etc).
   * @param covered the string of the number of covered code.
   * @param uncovered the string of the number of uncovered code.
   * @return The new SensorData CodeIssue instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeCoverage(String tstampString, String user, String runtimeString,
      String resource, String tool, String granularity, String covered, String uncovered)
    throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(runtimeString);
    SensorData data = new SensorData();
    data.setSensorDataType("Coverage");
    data.setOwner(user);
    data.setTimestamp(tstamp);
    data.setTool(tool);
    data.setResource(resource);
    data.setRuntime(runtime);

    if (granularity != null) {
      Property typeProperty = new Property();
      typeProperty.setKey("Granularity");
      typeProperty.setValue(granularity);
      Property coveredProperty = new Property();
      coveredProperty.setKey("Covered");
      coveredProperty.setValue(covered);
      Property uncoveredProperty = new Property();
      uncoveredProperty.setKey("Uncovered");
      uncoveredProperty.setValue(uncovered);

      Properties properties = new Properties();
      properties.getProperty().add(typeProperty);
      properties.getProperty().add(coveredProperty);
      properties.getProperty().add(uncoveredProperty);
      data.setProperties(properties);
    }
    return data;
  }
}
