package org.hackystat.dailyprojectdata.test;

import org.hackystat.dailyprojectdata.server.Server;
import org.junit.BeforeClass;
import static org.hackystat.dailyprojectdata.server.ServerProperties.SENSORBASE_HOST_KEY;

public class DailyProjectDataTestHelper {

  /** The DailyProjectData server used in these tests. */
  private static Server server;

  /**
   * Constructor.
   */
  public DailyProjectDataTestHelper () {
  }
  
  /**
   * Starts the server going for these tests. 
   * @throws Exception If problems occur setting up the server. 
   */
  @BeforeClass public static void setupServer() throws Exception {
    DailyProjectDataTestHelper.server = Server.newInstance();
  }

  /**
   * Returns the hostname associated with this DPD test server. 
   * @return The host name, including the context root. 
   */
  protected String getDailyProjectDataHostName() {
    return DailyProjectDataTestHelper.server.getHostName();
  }
  
  /**
   * Returns the sensorbase hostname that this DPD server communicates with.
   * @return The host name, including the context root. 
   */
  protected String getSensorBaseHostName() {
    return DailyProjectDataTestHelper.server.getServerProperties().get(SENSORBASE_HOST_KEY);
  }
}

