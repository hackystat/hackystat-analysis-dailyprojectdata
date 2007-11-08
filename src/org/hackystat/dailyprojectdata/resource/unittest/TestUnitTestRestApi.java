package org.hackystat.dailyprojectdata.resource.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.unittest.jaxb.MemberData;
import org.hackystat.dailyprojectdata.resource.unittest.jaxb.UnitTestDailyProjectData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Runs test to exercise DPDResource.
 *
 * @author Pavel Senin, Philip Johnson.
 *
 */
public class TestUnitTestRestApi extends DailyProjectDataTestHelper {

  /** The user for this test case. */
  private String user = "TestUnitTest@hackystat.org";

  /**
   * Test that GET {host}/unittest/{user}/default/{starttime} works properly. First, it creates a
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
    UnitTestTestHelper helper = new UnitTestTestHelper();
    batchData.getSensorData().add(helper.makePassUnitTest("2007-04-30T02:00:00", user));
    batchData.getSensorData().add(helper.makePassUnitTest("2007-04-30T02:10:00", user));
    batchData.getSensorData().add(helper.makeFailUnitTest("2007-04-29T23:55:00", user));
    batchData.getSensorData().add(helper.makeFailUnitTest("2007-05-01T00:01:00", user));

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
    UnitTestDailyProjectData unitDPD = dpdClient.getUnitTest(user, "Default", Tstamp
        .makeTimestamp("2007-04-30"));
    assertTrue("Checking user", user.equalsIgnoreCase(unitDPD.getOwner()));

    MemberData memberData = unitDPD.getMemberData().get(0);

    assertEquals("Checking Failures", BigInteger.valueOf(0), memberData.getFailure());

    assertEquals("Checking Success", BigInteger.valueOf(2), memberData.getSuccess());

  }
}
