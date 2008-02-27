package org.hackystat.dailyprojectdata.resource.unittest;

import static org.hackystat.dailyprojectdata.server.ServerProperties.SENSORBASE_FULLHOST_KEY;

import java.io.StringWriter;
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

import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.dailyprojectdata.resource.unittest.jaxb.MemberData;
import org.hackystat.dailyprojectdata.resource.unittest.jaxb.UnitTestDailyProjectData;
import org.hackystat.sensorbase.client.SensorBaseClient;
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
 * Implements the Resource for processing GET {host}/unittest/{user}/{projectname}/{timestamp}
 * requests. 
 * 
 * Authenticated user must be the uriUser, or Admin, or project member. 
 * 
 * @author Pavel Senin, Philip Johnson
 */

public class UnitTestResource extends DailyProjectDataResource {

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public UnitTestResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns an UnitTestDailyProjectData instance.
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    Logger logger = this.server.getLogger();
    logger.fine("UnitTest DPD: Starting");
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        // [2] get a SensorDataIndex of UnitTest sensor data for this Project on the requested day.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        logger.fine("UnitTest DPD: Requesting index: " + uriUser + " " + project);
        SensorDataIndex index = client.getProjectSensorData(uriUser, project, startTime, endTime,
            "UnitTest");
        // [3] Update the counter with this data.
        logger.fine("UnitTest DPD: Got index.  " + index.getSensorDataRef().size() + " instances");
        UnitTestCounter counter = new UnitTestCounter();
        for (SensorDataRef ref : index.getSensorDataRef()) {
          counter.add(client.getSensorData(ref));
        } 
        logger.fine("UnitTest DPD: Finished retrieving instances. "); 

        // return resulting data
        UnitTestDailyProjectData unitTestDPD = new UnitTestDailyProjectData();
        // create the individual MemberData elements.
        String sensorBaseHost = this.server.getServerProperties().get(SENSORBASE_FULLHOST_KEY);
        for (String member : counter.getMembers()) {
          MemberData memberData = new MemberData();
          memberData.setMemberUri(sensorBaseHost + "users/" + member);
          memberData.setSuccess(counter.getPassCount(member));
          memberData.setFailure(counter.getFailCount(member));
          unitTestDPD.getMemberData().add(memberData);
        }

        unitTestDPD.setOwner(uriUser);
        unitTestDPD.setProject(project);
        unitTestDPD.setStartTime(startTime);
        unitTestDPD.setUriPattern("**"); // we don't support UriPatterns yet.

        String xmlData = makeUnitTestDPD(unitTestDPD);
        logRequest("UnitTest");
        return super.getStringRepresentation(xmlData);

      }
      catch (Exception e) {
        server.getLogger().warning("Error processing UnitTest DPD: " + StackTrace.toString(e));
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        return null;
      }
    }
    return null;
  }

  /**
   * Returns the passed UnitTestDPD instance as a String encoding of its XML representation.
   * 
   * @param data The UnitTestDPD instance.
   * @return The XML String representation.
   * @throws Exception If problems occur during translation.
   */
  private String makeUnitTestDPD(UnitTestDailyProjectData data) throws Exception {
    JAXBContext unitTestJAXB = (JAXBContext) this.server.getContext().getAttributes().get(
        "UnitTestJAXB");
    Marshaller marshaller = unitTestJAXB.createMarshaller();
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
