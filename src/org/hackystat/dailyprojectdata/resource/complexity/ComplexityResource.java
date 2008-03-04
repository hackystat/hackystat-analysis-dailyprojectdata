package org.hackystat.dailyprojectdata.resource.complexity;

import java.io.StringWriter;
import java.util.List;
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

import org.hackystat.dailyprojectdata.resource.complexity.jaxb.ComplexityDailyProjectData;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.dailyprojectdata.resource.complexity.jaxb.FileData;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Implements the Resource for processing GET
 * {host}/complexity/{user}/{project}/{starttime}/{type} requests. Requires the
 * authenticated user to be {user}, the Admin, or a member of {project}.
 * 
 * @author Philip Johnson
 */
public class ComplexityResource extends DailyProjectDataResource {
  
  private String type;
  
  private String tool;

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public ComplexityResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.type = (String) request.getAttributes().get("type");
    this.tool = (String) request.getAttributes().get("tool");
  }

  /**
   * Returns a ComplexityDailyProjectData instance representing the Complexity
   * associated with each file for the Project data, or null if not authorized.
   * Authenticated user must be the uriUser, or Admin, or project member. 
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    Logger logger = this.server.getLogger();
    logger.fine("Complexity DPD: Starting");
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        
        // [2] Get the Snapshot containing the last sent FileMetric data for this Project.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        logger.fine("Complexity DPD: Requesting index: " + uriUser + " " + project);
        SensorDataIndex snapshot = (this.tool == null) ?
            // Get the latest snapshot from any tool.
            client.getProjectSensorDataSnapshot(this.uriUser, this.project, startTime, endTime, 
            "FileMetric") :
              // Get the latest snapshot from the specified tool.
              client.getProjectSensorDataSnapshot(this.uriUser, this.project, startTime, endTime, 
                  "FileMetric", this.tool);

        logger.fine("Complexity DPD: Got index: " + 
            snapshot.getSensorDataRef().size() + " instances. Now retrieving instances.");
        // [3] create and return the ComplexityDailyProjectData instance.
        ComplexityDailyProjectData fileDpd = new ComplexityDailyProjectData();
        fileDpd.setOwner(uriUser);
        fileDpd.setProject(project);
        fileDpd.setStartTime(startTime);
        fileDpd.setType(this.type);
        
        if (!snapshot.getSensorDataRef().isEmpty()) {
          for (SensorDataRef ref : snapshot.getSensorDataRef()) {
            SensorData data = client.getSensorData(ref);
            fileDpd.setOwner(data.getOwner());
            fileDpd.setTool(data.getTool());
            String property = this.type + "ComplexityList";
            String complexities = getProperty(data, property);
            if (complexities != null) { //NOPMD
              FileData fileData = new FileData();
              fileData.setFileUri(data.getResource());
              fileData.setComplexityValues(complexities);
              fileData.setTotalLines(getProperty(data, "TotalLines"));
              fileDpd.getFileData().add(fileData);
            }
          }
        }
        String xmlData = makeComplexityMetric(fileDpd);
        logRequest("Complexity");
        return super.getStringRepresentation(xmlData);
      } 
      catch (Exception e) {
        server.getLogger().warning("Error processing FileMetric DPD: " + StackTrace.toString(e));
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
    }
    return null;
  }

  /**
   * Returns the passed SensorData instance as a String encoding of its XML
   * representation.
   * 
   * @param data The SensorData instance.
   * @return The XML String representation.
   * @throws Exception If problems occur during translation.
   */
  private String makeComplexityMetric(ComplexityDailyProjectData data) throws Exception {
    JAXBContext complexityJAXB = (JAXBContext) this.server.getContext().getAttributes().get(
        "ComplexityJAXB");
    Marshaller marshaller = complexityJAXB.createMarshaller();
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
  
  /**
   * Returns the string value associated with the specified property key. If no
   * property with that key exists, null is returned.
   * @param data The sensor data instance whose property list will be searched.
   * @param key the property key to search for.
   * @return The property value with the specified key or null.
   */
  private String getProperty(SensorData data, String key) {
    List<Property> propertyList = data.getProperties().getProperty();
    for (Property property : propertyList) {
      if (key.equals(property.getKey())) {
        return property.getValue();
      }
    }
    return null;
  }
}
