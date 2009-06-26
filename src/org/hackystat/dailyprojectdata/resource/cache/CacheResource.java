package org.hackystat.dailyprojectdata.resource.cache;

import java.util.logging.Logger;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * This resource responds to requests of form:
 * <pre>DELETE {host}/cache</pre>
 * This one clears all entries associated with the authorized user; in other words, all projects
 * that this user owns will have any cached DPDs removed.
 * <p>
 * It also responds to 
 * <pre>DELETE {host}/cache/{user}/{project}</pre>
 * This one clears only those cached DPD instances for the specified project owned by that user. 
 * In this case, the authorized user must be in the project specified by (project, user). 
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
   * If deleting the entire cache, then the authUser must be the UriUser. 
   * If deleting a project cache, then the authUser must be in the project 
   * identified by UriUser and project.
   */
  @Override
  public void removeRepresentations() {
    Logger logger = this.server.getLogger();
    logger.fine(String.format("Delete cache: %s %s %s ", authUser, uriUser, project));
    try {
      // Delete entire cache for this user.
      if (this.uriUser == null) {
        // Invoke the clear operation on the entire user's cache. 
        super.server.getFrontSideCache().clear(authUser);
        logger.info(String.format("All DPD cache entries deleted for %s ", authUser));
        getResponse().setStatus(Status.SUCCESS_OK);
        return;
      }
      
      // Otherwise user and project specified. Return now if authUser not in project. 
      SensorBaseClient client = super.getSensorBaseClient();
      if (!client.inProject(uriUser, project)) {
        String msg = String.format("Authenticated user (%s) isn't in project (%s) owned by %s", 
            authUser, project, uriUser);
        setStatusError(msg);
        return;
      }
      
      // If we're here, we are OK to delete the cache associated with the user and project. 
      super.server.getFrontSideCache().clear(uriUser, project);
      logger.info(String.format("All DPD cache entries deleted for %s/%s. ", uriUser, project));
      return; 
    }
    catch (Exception e) {
      setStatusError("Error during cache deletion", e);
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
  public Representation represent(Variant variant) {
    getResponse().setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    return null;
  }

}
