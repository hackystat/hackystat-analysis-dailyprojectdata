package org.hackystat.dailyprojectdata.resource.ping;

import static org.hackystat.dailyprojectdata.server.ServerProperties.SENSORBASE_HOST_KEY;

import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * The PingResource responds to a GET {host}/ping with the string "DailyProjectData".
 * It responds to GET  {host}/ping?user={user}&password={password} with
 * "DailyProjectData authenticated" if the user and password are valid, and 
 * "DailyProjectData" if not valid. 
 * @author Philip Johnson
 */
public class PingResource extends DailyProjectDataResource {
  
  /** From the URI, if authentication is desired. */
  private String user; 
  /** From the URI, if authentication is desired. */
  private String password;
  
  /**
   * The standard constructor.
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public PingResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.user = (String) request.getAttributes().get("user");
    this.password = (String) request.getAttributes().get("password");
  }
  
  /**
   * Returns the string "DailyProjectData" or "DailyProjectData authenticated", 
   * depending upon whether credentials are passed as form parameters and whether
   * they are valid. 
   * @param variant The representational variant requested.
   * @return The representation as a string.  
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    String unauthenticated = "DailyProjectData";
    String authenticated = "DailyProjectData authenticated";
    // Don't try to authenticate unless the user has passed both a user and password. 
    if ((user == null) || (password == null)) {
      return new StringRepresentation(unauthenticated);
    }
    // There is a user and password. So, check the SensorBase to see. 
    String sensorBaseHost = server.getServerProperties().get(SENSORBASE_HOST_KEY);
    boolean OK = SensorBaseClient.isRegistered(sensorBaseHost, user, password);
    return new StringRepresentation((OK ? authenticated : unauthenticated));
  }
  

}
