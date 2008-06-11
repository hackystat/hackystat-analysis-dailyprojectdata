package org.hackystat.dailyprojectdata.resource.cache;

import java.util.logging.Logger;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * This resource responds to requests of form:
 * DELETE {host}/cache/{user}
 * by deleting the contents of the SensorData cache associated with that user. 
 * Note that the {user} and the authenticated user names must be the same. 
 * Thus, the only person who can delete a user cache is that user themselves. 
 * This restriction overcomes several problems: (1) it guarantees that there is an
 * instantiated SensorBaseClient
 * for that user, and (2) it avoids concurrency race conditions. This constraints are very important
 * given the current JCS implementation suffers from a concurrent access problem:
 * <https://issues.apache.org/jira/browse/JCS-31>.
 * The restriction means that there is no "admin" level cache deletion, but I think we can live
 * with that.  The admin can always figure out the emails/passwords and write a script to do the
 * deletion, or else bring down the server and manually delete the files.  
 * 
 * @author Philip Johnson
 */
public class CacheResource extends DailyProjectDataResource {

  /**
   * The default constructor.
   * @param context The context.
   * @param request The request.
   * @param response The response. 
   */
  public CacheResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns 200 if cache delete command succeeded. 
   * The authorized user must be the same as the user specified in the URI.
   */
  @Override
  public void delete() {
    Logger logger = this.server.getLogger();
    logger.fine("Delete cache starting, auth/uri user is: " + authUser + "/" + uriUser);
    try {
      // [1] Make sure the authorized user is the same as the uriUser
      if (!this.authUser.equals(this.uriUser)) {
        String msg = "Authenticated user (" + this.authUser + ") must be the URI user (" +
        this.uriUser + ")";
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        return;
      }
      // [2] Now get the associated sensorbaseclient and invoke the clear operation.
      super.getSensorBaseClient().clearCache();
      logger.info("Sensor data cache deleted for user: " + this.uriUser);
      getResponse().setStatus(Status.SUCCESS_OK);
      return;
    }
    catch (Exception e) {
      String msg = "Error occurred during cache deletion: " + StackTrace.toString(e);
      logger.info(msg);
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, msg);
      return;
    }
  }

  /**
   * Indicate the DELETE method is supported.
   * 
   * @return True.
   */
  @Override
  public boolean allowDelete() {
    return true;
  }

  /**
   * Indicate that GET is not supported.
   * 
   * @return False.
   */
  @Override
  public boolean allowGet() {
    return false;
  }

  /**
   * Get is not supported, but the method must be implemented.
   * 
   * @param variant Ignored.
   * @return Null.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    getResponse().setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    return null;
  }

}
