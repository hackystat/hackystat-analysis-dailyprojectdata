package org.hackystat.dailyprojectdata.server;

import java.util.HashMap;

import java.util.Map;

import org.hackystat.sensorbase.client.SensorBaseClient;
import org.restlet.Context;
import org.restlet.Guard;
import org.restlet.data.ChallengeScheme;

/**
 * Performs authentication of each HTTP request using HTTP Basic authentication. 
 * Checks user/password credentials by pinging SensorBase, then caching authentic 
 * user/password combinations.  If a cached user/password combo does not match the 
 * current user/password combo, then the SensorBase is pinged again (because maybe
 * the user has changed their password recently). 
 * @author Philip Johnson
 */
public class Authenticator extends Guard {
  
  /** A map containing previously verified credentials. */
  private Map<String, String> credentials = new HashMap<String, String>();
  
  /** The sensorbase host, such as "http://localhost:9876/sensorbase/" */
  private String sensorBaseHost;
  

  /**
   * Initializes this Guard to do HTTP Basic authentication.
   * @param context The server context.
   * @param sensorBaseHost The host, such as 'http://localhost:9876/sensorbase/'.
   */
  public Authenticator (Context context, String sensorBaseHost) {
    super(context, ChallengeScheme.HTTP_BASIC,  "DailyProjectData");
    this.sensorBaseHost = sensorBaseHost;
  }
  
  /**
   * Returns true if the passed credentials are OK.
   * @param identifier The account name.
   * @param secretCharArray The password. 
   * @return If the credentials are valid.
   */
  @Override protected boolean checkSecret(String identifier, char[] secretCharArray) {
    String secret = new String(secretCharArray);
    // Return true if the user/password credentials are in the cache. 
    if (credentials.containsKey(identifier) &&
        secret.equals(credentials.get(identifier))) {
      return true;
    }
    // Otherwise we check the credentials with the SensorBase.
    boolean isRegistered = SensorBaseClient.isRegistered(sensorBaseHost, identifier, secret);
    if (isRegistered) {
      credentials.put(identifier, secret);
    }
    return isRegistered;
  }
}
