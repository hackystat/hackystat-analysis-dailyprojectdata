package org.hackystat.dailyprojectdata.resource.ping;

import static org.junit.Assert.assertTrue;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.junit.Test;

/**
 * Tests the Ping REST API.
 * 
 * @author Philip Johnson
 */
public class TestPingRestApi extends DailyProjectDataTestHelper {

  /**
   * Test that GET {host}/ping returns the service name.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testPing() throws Exception {
    assertTrue("Checking ping", DailyProjectDataClient.isHost(getDailyProjectDataHostName()));
  }
}
