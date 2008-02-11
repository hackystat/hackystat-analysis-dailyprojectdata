package org.hackystat.dailyprojectdata.resource.filemetric;

import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.dailyprojectdata.resource.filemetric.jaxb.FileData;
import org.hackystat.dailyprojectdata.resource.filemetric.jaxb.FileMetricDailyProjectData;
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
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Implements the Resource for processing GET
 * {host}/filemetric/{user}/{project}/{starttime} requests. Requires the
 * authenticated user to be {user}, the Admin, or a member of {project}.
 * 
 * @author Cam Moore, Philip Johnson
 */
public class FileMetricResource extends DailyProjectDataResource {
  
  private String sizeMetric;

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public FileMetricResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.sizeMetric = (String) request.getAttributes().get("sizemetric");
  }

  /**
   * Returns an FileMetricDailyProjectData instance representing the FileMetric
   * associated with the sizeMetric for the Project data, or null if not authorized.
   * Authenticated user must be the uriUser, or Admin, or project member. 
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        
        // [2] Get the Snapshot containing the last sent FileMetric data for this Project.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        SensorDataIndex snapshot = 
          client.getProjectSensorDataSnapshot(this.uriUser, this.project, startTime, endTime, 
              "FileMetric");
        
        // [3] create and return the FileMetricDailyProjectData instance.
        double total = 0;
        FileMetricDailyProjectData fileDpd = new FileMetricDailyProjectData();
        fileDpd.setOwner(uriUser);
        fileDpd.setProject(project);
        fileDpd.setStartTime(startTime);
        fileDpd.setSizeMetric(this.sizeMetric);
        
        if (!snapshot.getSensorDataRef().isEmpty()) {
          for (SensorDataRef ref : snapshot.getSensorDataRef()) {
            SensorData data = client.getSensorData(ref);
            fileDpd.setOwner(data.getOwner());
            fileDpd.setTool(data.getTool());
            Double value = getNumberProperty(data, this.sizeMetric);
            if (value != null) { //NOPMD
              FileData fileData = new FileData();
              fileData.setFileUri(data.getResource());
              fileData.setSizeMetricValue(value);
              fileDpd.getFileData().add(fileData);
              total += value;
            }
          }
        }
        fileDpd.setTotal(total);
        String xmlData = makeFileMetric(fileDpd);
        logRequest("FileMetric");
        return super.getStringRepresentation(xmlData);
      } 
      catch (Exception e) {
        server.getLogger().warning("Error processing FileMetric DPD: " + StackTrace.toString(e));
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
  private String makeFileMetric(FileMetricDailyProjectData data) throws Exception {
    JAXBContext fileMetricJAXB = (JAXBContext) this.server.getContext().getAttributes().get(
        "FileMetricJAXB");
    Marshaller marshaller = fileMetricJAXB.createMarshaller();
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
   * Returns a Double as the value of key in data, or null if not found. Also null if
   * the property was found but could not be converted to a Double.
   * @param data The sensor data instance. 
   * @param key The key whose Double value is to be retrieved.
   * @return The value, as a double.
   */
  private Double getNumberProperty(SensorData data, String key) {
    String prop = getProperty(data, key);
    if (prop == null) {
      return null;
    }
    // If we got a property, then make it a Double.
    Double metricValue = null;
    try {
      metricValue = Double.parseDouble(getProperty(data, this.sizeMetric));
    }
    catch (Exception e) {
      this.server.getLogger().info("In FileMetric Resource, parse into Double failed: " + 
          getProperty(data, this.sizeMetric));
    }
    return metricValue;
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
