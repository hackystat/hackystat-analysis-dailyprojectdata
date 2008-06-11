package org.hackystat.dailyprojectdata.resource.cache;

import static org.junit.Assert.assertTrue;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.junit.Test;

/**
 * Tests the Cache clear.
 *  
 * @author Philip Johnson
 */
public class TestCacheRestApi extends DailyProjectDataTestHelper {

  /** The user for this test case. */
  private String user = "TestCoupling@hackystat.org";
  
  /**
   * Test that DELETE {host}/cache/{user} can be invoked successfully.
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void deleteCache() throws Exception {
    // Now connect to the DPD server.
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(),
        user, user);
    dpdClient.authenticate();
    assertTrue("Testing delete cache.", dpdClient.clearServerCache(user));
  }
}
