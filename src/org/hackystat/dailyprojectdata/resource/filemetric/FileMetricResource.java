package org.hackystat.dailyprojectdata.resource.filemetric;

import java.io.StringWriter;
import java.util.Iterator;
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
 * authenticated user to be {user} or else the Admin user for the sensorbase
 * connected to this service.
 * 
 * @author Cam Moore
 * 
 */
public class FileMetricResource extends DailyProjectDataResource {

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public FileMetricResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns an DevTimeDailyProjectData instance representing the FileMetric
   * associated with the Project data, or null if not authorized.
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
        // [2] get a SensorDataIndex of all sensor data for this Project on
        // the requested day.

        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        SensorDataIndex index = client.getProjectSensorData(authUser, project, startTime, endTime);
        // [3] look through this index for FileMetric sensor data, and update
        // the FileMetric counter.
        FileMetricCounter counter = new FileMetricCounter(client);
        for (SensorDataRef ref : index.getSensorDataRef()) {
          if (ref.getSensorDataType().equals("FileMetric")) {
            counter.add(ref);
          }
        }
        // [4] create and return the FileMetricDailyProjectData
        FileMetricDailyProjectData fileSize = new FileMetricDailyProjectData();
        // create the individual FileData elements.
        List<SensorData> fileInfo = counter.getFileMetrics();
        for (Iterator<SensorData> iter = fileInfo.iterator(); iter.hasNext();) {
          SensorData data = iter.next();
          FileData fileData = new FileData();
          fileData.setFileUri(data.getResource());
          fileData.setSizeMetricValue(counter.getTotalLines(data));
          fileSize.getFileData().add(fileData);
        }
        fileSize.setOwner(uriUser);
        fileSize.setProject(project);
        fileSize.setUriPattern("**"); // we don't support UriPatterns yet.
        fileSize.setSizeMetric("LOC");
        fileSize.setTotalSizeMetricValue(counter.getTotalLines());
        fileSize.setStartTime(counter.getLastTime());
        String xmlData = makeFileMetric(fileSize);
        return super.getStringRepresentation(xmlData);
      } catch (Exception e) {
        server.getLogger().warning("Error processing fileMetric: " + StackTrace.toString(e));
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
}
