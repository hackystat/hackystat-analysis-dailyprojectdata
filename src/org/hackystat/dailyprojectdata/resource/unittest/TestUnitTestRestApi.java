package org.hackystat.dailyprojectdata.resource.unittest;

import static org.junit.Assert.assertTrue;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.unittest.jaxb.UnitTestDailyProjectData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Runs test to exercise DPDResource.
 *
 * @author Pavel Senin.
 *
 */
public class TestUnitTestRestApi extends DailyProjectDataTestHelper {

  /** The user for this test case. */
  private String user = "TestUnitTest@hackystat.org";

  /**
   * Test that GET {host}/devtime/{user}/default/{starttime} works properly. First, it creates a
   * test user and sends some sample DevEvent data to the SensorBase. Then, it invokes the GET
   * request and checks to see that it obtains the right answer. Finally, it deletes the data and
   * the user.
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void getDefaultUnitTestDPD() throws Exception {
    // First, create a batch of DevEvent sensor data.
    SensorDatas batchData = new SensorDatas();
    UnitTestDPDTestHelper helper = new UnitTestDPDTestHelper();
    batchData.getSensorData().add(helper.makeUnitTestEvent("2007-04-30T02:00:00", user));
    batchData.getSensorData().add(helper.makeUnitTestEvent("2007-04-30T02:10:00", user));
    batchData.getSensorData().add(helper.makeUnitTestEvent("2007-04-29T23:55:00", user));
    batchData.getSensorData().add(helper.makeUnitTestEvent("2007-05-01T00:01:00", user));

    // Connect to the sensorbase and register the DailyProjectDataDevEvent user.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
    client.authenticate();
    // Send the sensor data to the SensorBase.
    client.putSensorDataBatch(batchData);

    // Now connect to the DPD server.
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(),
        user, user);
    dpdClient.authenticate();
    UnitTestDailyProjectData unitDPD = dpdClient.getUnitTestDPD(user, "Default", Tstamp
        .makeTimestamp("2007-04-30"));
    assertTrue("Checking default devTime", user.equalsIgnoreCase(unitDPD.getOwner()));
    // assertEquals("Checking MemberData size", 1, devTime.getMemberData().size());
  }

  // Map<String, String> keyValMap = new HashMap<String, String>();
  // keyValMap.put("Tool", "JUnit");
  // keyValMap.put("SensorDataType", "UnitTest");
  // keyValMap.put("DevEvent-Type", "Test");

  // // Required
  // keyValMap.put("Runtime", runtimeGregorian.toString());
  // keyValMap.put("Timestamp", startTimeGregorian.toString());
  // keyValMap.put("Name", name);
  // keyValMap.put("Resource", testCaset2Path(name));
  // keyValMap.put("Result", result);
  //
  // // Optional
  // keyValMap.put("ElapsedTime", Long.toString(elapsedTimeMillis));
  // keyValMap.put("TestName", testClassName);
  // keyValMap.put("TestCaseName", testCaseName);
  //
  // if (!stringFailureList.isEmpty()) {
  // keyValMap.put("FailureString", stringFailureList.get(0));
  // }
  //
  // if (!stringErrorList.isEmpty()) {
  // keyValMap.put("ErrorString", stringErrorList.get(0));
  // }

  // Timestamp=2007-10-27T18:45:28.416-06:00
  // Type=UnitTest
  // Tool=JUnit
  // LastMod=2007-10-27T15:22:13.230-10:00
  // Runtime=2007-10-27T19:20:49.176-06:00
  // Resource=C:\workspace\hackystat-sensor-ant\${src.dir}\org\hackystat\core\...
  // Owner=senin@hawaii.edu
  // Name=org.hackystat.core.installer.util.TestProxyProperty.testNormalFunctionality
  // Result=pass
  // ElapsedTime=4
  // FailureString=Value should be the same. expected:<[8]0> but was:<[9]0>
  // TestName=org.hackystat.core.installer.util.TestProxyProperty
  // TestCaseName=testNormalFunctionality
  // DevEvent-Type=Test
}
