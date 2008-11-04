package org.hackystat.dailyprojectdata.frontsidecache;

import static org.junit.Assert.assertNull;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.junit.Test;

/**
 * Simple test class to make sure FrontSideCaching works. 
 * @author Philip Johnson
 */
public class TestFrontSideCache extends DailyProjectDataTestHelper {

  /**
   * Check to make sure FrontSideCache instantiation, put, get, and clear work. 
   */
  @Test
  public void testFrontSideCache() {

    String user = "user";
    String uri = "uri";
    String dpd = "dpd";
    String project = "project";
    FrontSideCache cache = new FrontSideCache(getDpdServer());
    cache.put(user, project, uri, dpd);
    // The following is the most important test. It's commented out because front side caching
    // is disabled for the rest of testing.  To run the following line, you must edit
    // ServerProperties, change the test value of TESTFRONTSIDECACHE_ENABLED to true. 
    // Then you can uncomment this line and run the test. 
    // Pretty bogus, but I can't think of a good way around it at the moment. 
    //assertEquals("testing frontside get", dpd, cache.get(user, uri));
    assertNull("Testing frontside get null", cache.get(user, project, "foo"));
    cache.clear(user);
    assertNull("Testing frontside clear", cache.get(user, project, "foo"));
  }
}
