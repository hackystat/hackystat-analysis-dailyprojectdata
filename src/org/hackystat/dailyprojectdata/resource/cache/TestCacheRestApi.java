package org.hackystat.dailyprojectdata.resource.cache;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;
import org.hackystat.dailyprojectdata.resource.build.TestBuildRestApi;

/**
 * Tests the Cache clear.
 * 
 * @author Philip Johnson
 */
public class TestCacheRestApi extends DailyProjectDataTestHelper {

  /** The user for this test case. */
  private String user = "TestCache@hackystat.org";

  /**
   * Test that DELETE {host}/cache/{user} can be invoked successfully.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void deleteCacheServerSide() throws Exception {
    // Now connect to the DPD server.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(),
        user, user);
    dpdClient.authenticate();
    assertTrue("Testing delete cache.", dpdClient.clearServerCache(user));
  }

  /**
   * Tests that client-side cache clearing operations work as expected.
   * @throws Exception if problems occur.
   */
  @Test
  public void testCacheClientSide() throws Exception {
    // Create a batch of test build data
    String runtime = "2007-10-30T02:00:00";
    String SUCCESS = "success";
    String FAILURE = "failure";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(
        TestBuildRestApi.makeBuild("2007-10-30T02:00:00", user, runtime, "cruisecontrol", SUCCESS));
    batchData.getSensorData().add(
        TestBuildRestApi.makeBuild("2007-10-30T02:10:00", user, runtime, "local", FAILURE));
    batchData.getSensorData().add(
        TestBuildRestApi.makeBuild("2007-10-30T02:15:00", user, runtime, null, SUCCESS));
    batchData.getSensorData().add(
        TestBuildRestApi.makeBuild("2007-10-30T02:20:00", user, runtime, null, SUCCESS));
    batchData.getSensorData().add(
        TestBuildRestApi.makeBuild("2007-10-30T02:25:00", user, runtime, "local", FAILURE));
    // Connect to the sensorbase and register the DailyProjectDataCodeIssue user.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
    client.authenticate();
    // Send the sensor data to the SensorBase.
    client.putSensorDataBatch(batchData);

    // Now connect to the DPD server.
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(),
        user, user);
    dpdClient.authenticate();
    dpdClient.enableCaching("TestDpdCache", "testdpdcache", 1D, 1000L);
    dpdClient.clearCache();

    XMLGregorianCalendar requestTstamp = Tstamp.makeTimestamp("2007-10-30");

    // This thing should be cached.
    dpdClient.getBuild(user, "Default", requestTstamp, null);
    String dpdType = "build";
    assertEquals("Check initial cache", 1, dpdClient.cacheSize(dpdType));
    dpdClient.clearCache();
    assertEquals("Check cleared cache 1", 0, dpdClient.cacheSize(dpdType));

    // Add it back and check the dpd-specific clear.
    dpdClient.getBuild(user, "Default", requestTstamp, null);
    assertEquals("Check cache 2", 1, dpdClient.cacheSize(dpdType));
    dpdClient.clearCache("build");
    assertEquals("Check cleared cache 2", 0, dpdClient.cacheSize(dpdType));

    // Add it back and check the dpd-specific and timestamp clear.
    dpdClient.getBuild(user, "Default", requestTstamp, null);
    assertEquals("Check cache 3", 1, dpdClient.cacheSize(dpdType));
    dpdClient.clearCache("build", "2007-10-30");
    assertEquals("Check cleared cache 3", 0, dpdClient.cacheSize(dpdType));
  }
}
