package org.hackystat.dailyprojectdata.resource.codeissue;

import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * Implements the Resource for processing GET {host}/codeissue/{user}/{project}/{timestamp}
 * requests. Requires the authenticated user to be {user} or else the Admin user for the sensorbase
 * connected to this service.
 * 
 * @author jsakuda
 */
public class CodeIssueResource extends DailyProjectDataResource {

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public CodeIssueResource(Context context, Request request, Response response) {
    super(context, request, response);
    // TODO Auto-generated constructor stub
  }

  /**
   * Returns a CodeIssueDailyProjectData instance representing the CodeIssues associated with the 
   * Project data, or null if not authorized. 
   * @param variant The representational variant requested.
   * @return The representation. 
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

}
