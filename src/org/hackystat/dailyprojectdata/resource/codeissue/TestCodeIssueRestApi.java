package org.hackystat.dailyprojectdata.resource.codeissue;

import static org.junit.Assert.assertSame;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueDailyProjectData;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.MemberData;
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
 * Tests the CodeIssue portion of the DailyProjectData REST API.
 * 
 * @author jsakuda
 */
public class TestCodeIssueRestApi extends DailyProjectDataTestHelper {
  /** Constant for Project. */
  private static final String PROJECT = "Default";
  
  /** Constant to make PMD happy. */
  private static final String CHECKSTYLE = "Checkstyle";
  
  /** The user for this test case. */
  private String user = "TestCodeIssue@hackystat.org";

  /**
   * Test that GET {host}/codeissue/{user}/default/{starttime} works properly. First, it
   * creates a test user and sends some sample CodeIssue data to the SensorBase. Then, it
   * invokes the GET request and checks to see that it obtains the right answer. Finally, it
   * deletes the data and the user.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testGetCodeIssues() throws Exception {
    // First, create a batch of DevEvent sensor data.
    String runtime = "2007-10-30T02:00:00";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:00:00", user, runtime, "PMD", null));
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:10:00", user, runtime, CHECKSTYLE, "AvoidStarImport"));
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:15:00", user, runtime, CHECKSTYLE, "AvoidStarImport"));
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:20:00", user, runtime, CHECKSTYLE, "PackageHtml"));
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:25:00", user, runtime, CHECKSTYLE, "LineLength"));
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:30:00", user, runtime, CHECKSTYLE, "LineLength"));

    // Connect to the sensorbase and register the DailyProjectDataCodeIssue user.
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
    CodeIssueDailyProjectData codeIssue = dpdClient.getCodeIssue(user, PROJECT, requestTstamp, 
        null, null);
    // There should be 4 member data entries because there are 4 tool/category combinations
    assertSame("Checking for 4 code issue entries.", 4,  codeIssue.getMemberData().size());
    
    codeIssue = dpdClient.getCodeIssue(user, PROJECT, requestTstamp, "PMD", null);
    List<MemberData> memberDatas = codeIssue.getMemberData();
    assertSame("Checking for 1 PMD member data instance.", 1, memberDatas.size());
    MemberData memberData = memberDatas.get(0);
    assertSame("Code issue count should be 0.", 0, memberData.getCodeIssues());
    
    codeIssue = dpdClient.getCodeIssue(user, PROJECT, requestTstamp, CHECKSTYLE, 
        "AvoidStarImport");
    memberDatas = codeIssue.getMemberData();
    assertSame("Checking for 1 Checkstyle member data instance.", 1, memberDatas.size());
    memberData = memberDatas.get(0);
    assertSame("Code issue count should be 2.", 2, memberData.getCodeIssues());
    
    codeIssue = dpdClient.getCodeIssue(user, PROJECT, requestTstamp, null, "PackageHtml");
    memberDatas = codeIssue.getMemberData();
    assertSame("Checking for 1 Checkstyle member data instance.", 1, memberDatas.size());
    memberData = memberDatas.get(0);
    assertSame("Code issue count should be 1.", 1, memberData.getCodeIssues());
    
    // First, delete all sensor data sent by this user. 
    SensorDataIndex index = client.getSensorDataIndex(user);
    for (SensorDataRef ref : index.getSensorDataRef()) {
      client.deleteSensorData(user, ref.getTimestamp());
    }
    // Now delete the user too.
    client.deleteUser(user);
  }

  /**
   * Creates a sample SensorData CodeIssue instance given a timestamp and a user.
   * 
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @param runtimeString The runtime.
   * @param tool The tool.
   * @param type The type of issue.
   * @return The new SensorData CodeIssue instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeCodeIssue(String tstampString, String user, String runtimeString,
      String tool, String type) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(runtimeString);
    SensorData data = new SensorData();
    data.setSensorDataType("CodeIssue");
    data.setOwner(user);
    data.setTimestamp(tstamp);
    data.setTool(tool);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(runtime);

    if (type != null) {
      Property typeProperty = new Property();
      typeProperty.setKey("Type");
      typeProperty.setValue(type);

      Properties properties = new Properties();
      properties.getProperty().add(typeProperty);
      data.setProperties(properties);
    }
    return data;
  }
}
