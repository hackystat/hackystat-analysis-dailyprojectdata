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
 * <p>
 * Because DPD resources will always want to communicate with the underlying SensorBase, this 
 * Authenticator also creates a map of user-to-SensorBaseClient instances which can be retrieved
 * from the server context.  This keeps the user password info in this class, while making the 
 * SensorBaseClient instance available to the service. 
 * @author Philip Johnson
 */
public class Authenticator extends Guard {
  
  /** A map containing previously verified credentials. */
  private Map<String, String> credentials = new HashMap<String, String>();
  
  /** A map containing SensorBaseClient instances, one per credentialed user. */
  private Map<String, SensorBaseClient> userClientMap = new HashMap<String, SensorBaseClient>();
  
  /** The sensorbase host, such as "http://localhost:9876/sensorbase/" */
  private String sensorBaseHost;
  
  /** The key to be used to retrieve the sensorbaseclient map from the server context. */
  public static final String AUTHENTICATOR_SENSORBASECLIENTS_KEY = "authenticator.clients";

  /**
   * Initializes this Guard to do HTTP Basic authentication.
   * Puts the credentials map in the server context so that Resources can get the 
   * password associated with the uriUser for their own invocations to the SensorBase. 
   * @param context The server context.
   * @param sensorBaseHost The host, such as 'http://localhost:9876/sensorbase/'.
   */
  public Authenticator (Context context, String sensorBaseHost) {
    super(context, ChallengeScheme.HTTP_BASIC,  "DailyProjectData");
    this.sensorBaseHost = sensorBaseHost;
    context.getAttributes().put(AUTHENTICATOR_SENSORBASECLIENTS_KEY, userClientMap);
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
      // Credentials are good, so save them and create a sensorbase client for this user. 
      credentials.put(identifier, secret);
      SensorBaseClient client = new SensorBaseClient(sensorBaseHost, identifier, secret);
      // Get the ServerProperties instance so we can determine if caching is enabled.
      Server server = (Server)getContext().getAttributes().get("DailyProjectDataServer");
      ServerProperties props = server.getServerProperties();
      if (props.isCacheEnabled()) {
        client.enableCaching(identifier, "dailyprojectdata", 
            props.getCacheMaxLife(), props.getCacheCapacity());
      }
      userClientMap.put(identifier, client);
    }
    return isRegistered;
  }
}
