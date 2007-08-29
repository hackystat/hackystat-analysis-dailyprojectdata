package org.hackystat.dailyprojectdata.resource.ping;

import static org.junit.Assert.assertTrue;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.junit.Test;

/**
 * Tests the Ping REST API.
 * 
 * @author Philip Johnson
 */
public class TestPingRestApi extends DailyProjectDataTestHelper {

  /**
   * Test that GET {host}/ping returns "DailyProjectData", and that
   * GET {host}/ping?user={user}&password={password} returns "DailyProjectData authenticated". 
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testPing() throws Exception {
    //First, just call isHost, which uses the standard ping. 
    String dpdHost = getDailyProjectDataHostName();
    assertTrue("Checking ping", DailyProjectDataClient.isHost(dpdHost));
    //Next, check authenticated ping. 
    String user = "TestDpdPing@hackystat.org";
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    DailyProjectDataClient client = new DailyProjectDataClient(dpdHost, user, user);
    client.authenticate();    
  }
}
