package org.hackystat.dailyprojectdata.resource.cache;

import java.util.logging.Logger;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * This resource responds to requests of form:
 * DELETE {host}/cache/{user}
 * DELETE {host}/cache/{user}/{project}
 * by clearing the contents of the (front-side) DPD cache associated with that user (and project,
 * if supplied). 
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
        String msg = String.format("Authenticated user (%s) isn't UriUser (%s)", authUser, uriUser);
        setStatusError(msg);
        return;
      }
      // [2] Now invoke the clear operation.
      if (this.project == null) {
        super.server.getFrontSideCache().clear(uriUser);
        logger.info(String.format("DPD cache deleted for %s ", uriUser));
      }
      else {
        super.server.getFrontSideCache().clear(uriUser, project);
        logger.info(String.format("DPD cache deleted for %s/%s. ", uriUser, project));
      }
      getResponse().setStatus(Status.SUCCESS_OK);
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
  public Representation getRepresentation(Variant variant) {
    getResponse().setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    return null;
  }

}
