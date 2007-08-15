package org.hackystat.dailyprojectdata.resource.dailyprojectdata;

import org.hackystat.dailyprojectdata.server.Server;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;

/**
 * An abstract superclass for all DailyProjectData resources that supplies common 
 * initialization processing. 
 * This includes:
 * <ul>
 * <li> Extracting the authenticated user identifier (when authentication available)
 * <li> Extracting the user email from the URI (when available)
 * <li> Declares that the TEXT/XML representational variant is supported.
 * </ul>
 * 
 * @author Philip Johnson
 *
 */
public abstract class DailyProjectDataResource extends Resource {
  
  /** To be retrieved from the URL as the 'email' template parameter, or null. */
  protected String uriUser = null; 
  
  /** The authenticated user, retrieved from the ChallengeResponse, or null */
  protected String authUser = null;
  
  /** The server. */
  protected Server server;
  
  /** The standard error message returned from invalid authentication. */
  protected String badAuth = "User is not admin and authenticated user does not not match URI user";
  
  /**
   * Provides the following representational variants: TEXT_XML.
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public DailyProjectDataResource(Context context, Request request, Response response) {
    super(context, request, response);
    if (request.getChallengeResponse() != null) {
      this.authUser = request.getChallengeResponse().getIdentifier();
    }
    this.server = (Server)getContext().getAttributes().get("DailyProjectDataServer");
    this.uriUser = (String) request.getAttributes().get("user");
    getVariants().clear(); // copied from BookmarksResource.java, not sure why needed.
    getVariants().add(new Variant(MediaType.TEXT_XML));
  }

  /**
   * The Restlet getRepresentation method which must be overridden by all concrete Resources.
   * @param variant The variant requested.
   * @return The Representation. 
   */
  @Override
  public abstract Representation getRepresentation(Variant variant);
  
}