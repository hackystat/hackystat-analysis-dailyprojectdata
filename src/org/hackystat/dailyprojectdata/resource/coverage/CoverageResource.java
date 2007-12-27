package org.hackystat.dailyprojectdata.resource.coverage;

import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hackystat.dailyprojectdata.resource.coverage.jaxb.ConstructData;
import org.hackystat.dailyprojectdata.resource.coverage.jaxb.CoverageDailyProjectData;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.dailyprojectdata.resource.snapshot.SensorDataSnapshot;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.hackystat.utilities.time.period.Day;
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
 * {host}/coverage/{user}/{project}/{timestamp}/{type} requests. 
 * 
 * Authenticated user must be the uriUser, or Admin, or project member. 
 * 
 * @author jsakuda
 * @author austen
 */
public class CoverageResource extends DailyProjectDataResource {
  private final String granularity;

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public CoverageResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.granularity = (String) request.getAttributes().get("type");
  }

  /**
   * Returns an CoverageDailyProjectData instance representing the Coverage
   * associated with the Project data, or null if not authorized.
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();

        // [2] Get all Coverage data for this Project on the requested day.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        Day day = Day.getInstance(startTime);
        SensorDataSnapshot snapshot = new SensorDataSnapshot(client, this.authUser,
            this.project, "Coverage", day);

        //[3] Load the data into a data container to abstract retrieval.
        CoverageDataContainer dataContainer = new CoverageDataContainer();
        for (Iterator<SensorData> i = snapshot.iterator(); i.hasNext();) {
          dataContainer.addCoverageData(i.next());
        }

        // [4] Get the latest batch of data for each project member.
        CoverageDailyProjectData coverageData = new CoverageDailyProjectData();
        for (String owner : dataContainer.getOwners()) {
          for (CoverageData data : dataContainer.getData(owner)) {
            ConstructData constructData = new ConstructData();
            constructData.setName(data.getResource());
            constructData.setNumCovered(data.getCovered(this.granularity));
            constructData.setNumUncovered(data.getUncovered(this.granularity));
            coverageData.getConstructData().add(constructData);
          }
        }

        coverageData.setStartTime(dataContainer.getRuntime());
        coverageData.setOwner(uriUser);
        coverageData.setProject(project);
        String xmlData = this.makeCoverage(coverageData);
        return super.getStringRepresentation(xmlData);
      }
      catch (Exception e) {
        server.getLogger().warning("Error processing Coverage DPD: " + StackTrace.toString(e));
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
  private String makeCoverage(CoverageDailyProjectData data) throws Exception {
    JAXBContext codeIssueJAXB = (JAXBContext) this.server.getContext().getAttributes().get(
        "CoverageJAXB");
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
