package org.hackystat.dailyprojectdata.resource.coverage;

import static org.junit.Assert.assertSame;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.coverage.jaxb.CoverageDailyProjectData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the Coverage portion of the DailyProjectData REST API.
 * 
 * @author jsakuda
 * @author austen
 */
public class TestCoverageRestApi extends DailyProjectDataTestHelper {
  /** Constant for Project. */
  private static final String PROJECT = "Default";
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
        TestCoverageData.createData("2007-10-30T02:00:00", runtime, user, "file://Foo.java"));
    batchData.getSensorData().add(
        TestCoverageData.createData("2007-10-30T02:10:00", runtime, user, "file://Foo2.java"));
    batchData.getSensorData().add(
        TestCoverageData.createData("2007-10-30T02:15:00", runtime, user, "file://Foo3.java"));
    batchData.getSensorData().add(
        TestCoverageData.createData("2007-10-30T02:20:00", runtime, user, "file://Foo4.java"));

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
        requestTstamp, "line");
    assertSame("Checking for 4 coverage entries with a 'line' granularity.", 4, lineCoverage
        .getConstructData().size());

    CoverageDailyProjectData classCoverage = dpdClient.getCoverage(user, PROJECT,
        requestTstamp, "class");
    assertSame("Checking for 4 coverage entries with a 'class' granularity.", 4, classCoverage
        .getConstructData().size());

    // Then, delete all sensor data sent by this user.
    SensorDataIndex index = client.getSensorDataIndex(user);
    for (SensorDataRef ref : index.getSensorDataRef()) {
      client.deleteSensorData(user, ref.getTimestamp());
    }
    // Now delete the user too.
    client.deleteUser(user);
  }
}
