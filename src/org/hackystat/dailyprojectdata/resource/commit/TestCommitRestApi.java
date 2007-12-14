package org.hackystat.dailyprojectdata.resource.commit;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.commit.jaxb.CommitDailyProjectData;
import org.hackystat.dailyprojectdata.resource.commit.jaxb.MemberData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the Commit portion of the DailyProjectData REST API.
 * 
 * @author austen
 */
public class TestCommitRestApi extends DailyProjectDataTestHelper {
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
  public void testGetCommit() throws Exception {
    // First, create a batch of data.
    String runtime = "2007-10-30T02:00:00";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(
        TestCommitData.createData("2007-10-30T02:10:00", runtime, user, "C:\\foo.java", "10",
            "20", "30"));
    batchData.getSensorData().add(
        TestCommitData.createData("2007-10-30T02:11:00", runtime, user, "C:\\foo2.java", "40",
            "50", "60"));

    // Then, create a batch of unrelated data.
    batchData.getSensorData().add(
        TestCommitData.createData("2007-11-30T02:15:00", "2007-11-30T02:15:00", user,
            "C:\\foo3.java", "70", "80", "90"));
    batchData.getSensorData().add(
        TestCommitData.createData("2007-11-30T02:16:00", "2007-11-30T02:15:00", user,
            "C:\\foo4.java", "100", "110", "120"));

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
    CommitDailyProjectData commitDpd = dpdClient.getCommit(user, PROJECT, requestTstamp);
    assertSame("Checking for 1 commit entries", 1, commitDpd.getMemberData().size());
    MemberData memberData = commitDpd.getMemberData().get(0);
    assertEquals("The amount of commits is incorrect.", Integer.valueOf(2), memberData
        .getCommits());
    assertEquals("The amount of lines added is incorrect.", Integer.valueOf(50), memberData
        .getLinesAdded());
    assertEquals("The amount of lines deleted is incorrect.", Integer.valueOf(70), memberData
        .getLinesDeleted());
    assertEquals("The amount of lines changed is incorrect.", Integer.valueOf(90), memberData
        .getLinesChanged());

    // Then, delete all sensor data sent by this user.
    SensorDataIndex index = client.getSensorDataIndex(user);
    for (SensorDataRef ref : index.getSensorDataRef()) {
      client.deleteSensorData(user, ref.getTimestamp());
    }
    // Now delete the user too.
    client.deleteUser(user);
  }
}
