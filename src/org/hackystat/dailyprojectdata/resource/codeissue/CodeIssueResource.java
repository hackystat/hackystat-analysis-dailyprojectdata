package org.hackystat.dailyprojectdata.resource.codeissue;

import java.io.StringWriter;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueDailyProjectData;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueData;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Implements the Resource for processing GET {host}/codeissue/{user}/{project}/{timestamp}
 * requests. 
 * 
 * Authenticated user must be the uriUser, or Admin, or project member. 
 * 
 * @author Philip Johnson, Julie Sakuda.
 */
public class CodeIssueResource extends DailyProjectDataResource {

  /** The optional code issue tool. */
  private String tool;

  /** The optional type. */
  private String type;

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public CodeIssueResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.tool = (String) request.getAttributes().get("Tool");
    // get the type and remove any spaces
    this.type = (String) request.getAttributes().get("Type");
    if (this.type != null) {
      this.type = type.replaceAll(" ", "");
    }
  }

  /**
   * Returns a CodeIssueDailyProjectData instance representing the CodeIssues associated with
   * the Project data, or null if not authorized.
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.server.getLogger();
    logger.fine("CodeIssue DPD: Starting");
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        // [2] Check the front side cache and return if the DPD is found and is OK to access.
        String cachedDpd = this.server.getFrontSideCache().get(uriUser, project, uriString);
        if ((cachedDpd != null) && client.inProject(uriUser, project)) {
          return super.getStringRepresentation(cachedDpd);
        }
        // [2] get a SensorDataIndex of all CodeIssue data for this Project on the requested day.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        logger.fine("CodeIssue DPD: Requesting index: " + uriUser + " " + project);
        SensorDataIndex index = client.getProjectSensorData(uriUser, project, startTime,
            endTime, "CodeIssue");
        logger.fine("CodeIssue DPD: Got index: " + index.getSensorDataRef().size() + " instances");

        // [3] Create a MultiToolSnapshot generated from all CodeIssue sensor data for this day.
        MultiToolSnapshot snapshot = new MultiToolSnapshot();
        for (SensorDataRef ref : index.getSensorDataRef()) {
          SensorData data = client.getSensorData(ref);
          snapshot.add(data);
        }
        logger.fine("CodeIssue DPD: retrieved all instances. Now building DPD.");
        // [4] Create the codeIssue DPD. 
        CodeIssueDailyProjectData codeIssue = new CodeIssueDailyProjectData();
        
        // [4.1] Case 1: tool and type are null. Add an entry for all CodeIssueTypes in all tools.
        if ((this.tool == null ) && (this.type == null)) {
          for (String tool : snapshot.getTools()) {
            Set<SensorData> toolSnapshot = snapshot.getSensorData(tool);
            IssueTypeCounter counter = new IssueTypeCounter(toolSnapshot, logger);
            for (String issueType : counter.getTypes()) {
              codeIssue.getCodeIssueData().add(makeCodeIssueData(tool, issueType, counter));
            }
            // Add a zero entry if necessary.
            if (counter.getTypes().isEmpty()) { //NOPMD
              codeIssue.getCodeIssueData().add(makeZeroCodeIssueData(tool));
            }
          }
        }
        
        // [4.2] Case 2: tool is specified, type is null. Add entry for all types for this tool.
        if ((this.tool != null ) && (this.type == null)) {
          Set<SensorData> toolSnapshot = snapshot.getSensorData(this.tool);
          IssueTypeCounter counter = new IssueTypeCounter(toolSnapshot, this.getLogger());
          for (String issueType : counter.getTypes()) {
            codeIssue.getCodeIssueData().add(makeCodeIssueData(this.tool, issueType, counter));
          }
          // Add a zero entry if we have data for this tool, but no issues.
          if (!toolSnapshot.isEmpty() && counter.getTypes().isEmpty()) { //NOPMD
            codeIssue.getCodeIssueData().add(makeZeroCodeIssueData(tool));
          }
        }
        
        // [4.3] Case 3: type is specified, tool is null. Add entry for all occurrences of this type
        if ((this.tool == null ) && (this.type != null)) {
          for (String tool : snapshot.getTools()) {
            Set<SensorData> toolSnapshot = snapshot.getSensorData(tool);
            IssueTypeCounter counter = new IssueTypeCounter(toolSnapshot, this.getLogger());
            for (String issueType : counter.getTypes()) {
              if (issueType.equals(this.type)) { //NOPMD
                codeIssue.getCodeIssueData().add(makeCodeIssueData(tool, issueType, counter));
              }
            }
          }
          // Not sure how to indicate 'zero' in this case.  So don't try.
        }
        
        // [4.4] Case 4: tool and type are specified.  Add entry for this tool and this type.
        if ((this.tool != null ) && (this.type != null)) {
          Set<SensorData> toolSnapshot = snapshot.getSensorData(this.tool);
          IssueTypeCounter counter = new IssueTypeCounter(toolSnapshot, this.getLogger());
          for (String issueType : counter.getTypes()) {
            if (this.type.equals(issueType)) { //NOPMD
              codeIssue.getCodeIssueData().add(makeCodeIssueData(this.tool, issueType, counter));
            }
          }
          // Add a zero entry if we have data for this tool, but no issues of that type
          if (!toolSnapshot.isEmpty() && !counter.getTypes().contains(this.type)) { //NOPMD
            codeIssue.getCodeIssueData().add(makeZeroCodeIssueData(tool));
          }
        }
        
        // Now finish building the structure
        codeIssue.setStartTime(startTime);
        codeIssue.setOwner(uriUser);
        codeIssue.setProject(project);
        codeIssue.setUriPattern("**"); // we don't support UriPatterns yet.

        String xmlData = this.makeCodeIssue(codeIssue);
        if (!Tstamp.isTodayOrLater(startTime)) {
          this.server.getFrontSideCache().put(uriUser, project, uriString, xmlData);
        }
        logRequest("CodeIssue", this.tool, this.type);
        return super.getStringRepresentation(xmlData);
      }
      catch (Exception e) {
        setStatusError("Error creating CodeIssue DPD.", e);
        return null;
      }
    }
    return null;
  }

  
  /**
   * Creates a returns a CodeIssueData instance.
   * @param tool The Tool. 
   * @param issueType The Issue Type.
   * @param counter The CodeIssueCounter.
   * @return The CodeIssueData instance. 
   */
  private CodeIssueData makeCodeIssueData(String tool, String issueType, IssueTypeCounter counter) {
    CodeIssueData issueData = new CodeIssueData();
    issueData.setTool(tool);
    issueData.setIssueType(issueType);
    issueData.setNumIssues(counter.getCount(issueType));
    return issueData;
  }
  
  /**
   * Creates a zero CodeIssueData entry for the specified tool.
   * @param tool The tool with zero issues. 
   * @return The "zero" CodeIssueData entry for this tool.
   */
  private CodeIssueData makeZeroCodeIssueData(String tool) {
    CodeIssueData issueData = new CodeIssueData();
    issueData.setTool(tool);
    issueData.setNumIssues(0);
    return issueData;
  }

  /**
   * Returns the passed SensorData instance as a String encoding of its XML representation.
   * 
   * @param data The SensorData instance.
   * @return The XML String representation.
   * @throws Exception If problems occur during translation.
   */
  private String makeCodeIssue(CodeIssueDailyProjectData data) throws Exception {
    JAXBContext codeIssueJAXB = (JAXBContext) this.server.getContext().getAttributes().get(
        "CodeIssueJAXB");
    Marshaller marshaller = codeIssueJAXB.createMarshaller();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
    Document doc = documentBuilder.newDocument();
    marshaller.marshal(data, doc);
    DOMSource domSource = new DOMSource(doc);
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.transform(domSource, result);
    return writer.toString();
  }
}
