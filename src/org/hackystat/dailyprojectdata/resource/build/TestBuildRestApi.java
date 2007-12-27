package org.hackystat.dailyprojectdata.resource.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.build.jaxb.BuildDailyProjectData;
import org.hackystat.dailyprojectdata.resource.build.jaxb.MemberData;
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
 * Tests the Build portion of the DailyProjectData REST API.
 * 
 * @author jsakuda
 */
public class TestBuildRestApi extends DailyProjectDataTestHelper {
  /** Success result. */
  private static final String SUCCESS = "Success";
  /** Failure result. */
  private static final String FAILURE = "Failure";

  /** The user for this test case. */
  private String user = "TestBuild@hackystat.org";

  /**
   * Test that GET {host}/build/{user}/Default/{starttime} works properly. First, it creates a
   * test user and sends some sample Build data to the SensorBase. Then, it invokes the GET
   * request and checks to see that it obtains the right answer. Finally, it deletes the data
   * and the user.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testGetBuilds() throws Exception {
    // Create a batch of test build data
    String runtime = "2007-10-30T02:00:00";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(
        makeBuild("2007-10-30T02:00:00", user, runtime, "cruisecontrol", SUCCESS));
    batchData.getSensorData().add(
        makeBuild("2007-10-30T02:10:00", user, runtime, "local", FAILURE));
    batchData.getSensorData().add(
        makeBuild("2007-10-30T02:15:00", user, runtime, null, SUCCESS));
    batchData.getSensorData().add(
        makeBuild("2007-10-30T02:20:00", user, runtime, null, SUCCESS));
    batchData.getSensorData().add(
        makeBuild("2007-10-30T02:25:00", user, runtime, "local", FAILURE));

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
    
    BuildDailyProjectData build = dpdClient.getBuild(user, "Default", requestTstamp, null);
    List<MemberData> memberData = build.getMemberData();
    assertEquals("Should only have 1 member entry.", 1, memberData.size());

    MemberData data = memberData.get(0);
    assertSame("Should have 2 failures.", 2, data.getFailure());
    assertSame("Should have 3 successes.", 3, data.getSuccess());
    
    // test Type=*
    build = dpdClient.getBuild(user, "Default", requestTstamp, "*");
    memberData = build.getMemberData();
    assertEquals("Should only have 1 member entry.", 1, memberData.size());

    data = memberData.get(0);
    assertSame("Should have 2 failures.", 2, data.getFailure());
    assertSame("Should have 3 successes.", 3, data.getSuccess());
    
    
    // test Type = "local"
    build = dpdClient.getBuild(user, "Default", requestTstamp, "local");
    memberData = build.getMemberData();
    assertSame("Should only have 1 member entry.", 1, memberData.size());

    data = memberData.get(0);
    assertSame("Should have 2 failures.", 2, data.getFailure());
    assertSame("Should have 0 successes.", 0, data.getSuccess());

    // First, delete all sensor data sent by this user.
    SensorDataIndex index = client.getSensorDataIndex(user);
    for (SensorDataRef ref : index.getSensorDataRef()) {
      client.deleteSensorData(user, ref.getTimestamp());
    }
    // Now delete the user too.
    client.deleteUser(user);
  }
  
  /**
   * Creates a sample SensorData Build instance.
   * 
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @param runtimeString The runtime.
   * @param type The optional type of the build.
   * @param result "Success" or "Failure".
   * @return The new SensorData Build instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeBuild(String tstampString, String user, String runtimeString,
      String type, String result) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(runtimeString);
    SensorData data = new SensorData();
    data.setSensorDataType("Build");
    data.setOwner(user);
    data.setTimestamp(tstamp);
    data.setTool("Ant");
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(runtime);

    data.setProperties(new Properties());
    Properties properties = data.getProperties();

    Property resultProperty = new Property();
    resultProperty.setKey("Result");
    resultProperty.setValue(result);
    properties.getProperty().add(resultProperty);

    if (type != null) {
      Property property = new Property();
      property.setKey("Type");
      property.setValue(type);
      properties.getProperty().add(property);
    }

    return data;
  }
}
