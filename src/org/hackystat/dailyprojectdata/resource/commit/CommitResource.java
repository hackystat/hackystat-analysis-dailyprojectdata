package org.hackystat.dailyprojectdata.resource.commit;

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

import org.hackystat.dailyprojectdata.resource.commit.jaxb.CommitDailyProjectData;
import org.hackystat.dailyprojectdata.resource.commit.jaxb.MemberData;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.sensorbase.client.SensorBaseClient;
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
 * Implements the Resource for processing GET
 * {host}/commit/{user}/{project}/{timestamp}/ requests. 
 * 
 * Authenticated user must be the uriUser, or Admin, or project member. 
 * 
 * @author jsakuda
 * @author austen
 */
public class CommitResource extends DailyProjectDataResource {
  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public CommitResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns an CoverageDailyProjectData instance representing the Coverage
   * associated with the Project data, or null if not authorized.
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    Logger logger = this.server.getLogger();
    logger.fine("Commit DPD: Starting");
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        // [2] get a SensorDataIndex of all Coverage data for this Project on
        // the requested day.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        logger.fine("Commit DPD: Requesting index: " + uriUser + " " + project);
        SensorDataIndex index = client.getProjectSensorData(this.uriUser, this.project,
            startTime, endTime, "Commit");
        logger.fine("Commit DPD: Got index: " + index.getSensorDataRef().size() + " instances");

        // [3] Add all of the appropriate data to the data container.
        CommitDataContainer container = new CommitDataContainer();
        for (SensorDataRef ref : index.getSensorDataRef()) {
          container.addCommitData(client.getSensorData(ref));
        }
        logger.fine("Commit DPD: retrieved instances, now building the DPD instance.");

        // [4] Get the aggregate data for each project member.
        String sensorBaseHost = this.server.getServerProperties().get(SENSORBASE_FULLHOST_KEY);
        CommitDailyProjectData commitData = new CommitDailyProjectData();
        for (String owner : container.getOwners()) {
          MemberData memberData = new MemberData();
          memberData.setMemberUri(sensorBaseHost + "users/" + owner);
          memberData.setCommits(container.getCommits(owner));
          memberData.setLinesAdded(container.getLinesAdded(owner));
          memberData.setLinesDeleted(container.getLinesDeleted(owner));
          memberData.setLinesModified(container.getLinesModified(owner));
          commitData.getMemberData().add(memberData);
        }

        commitData.setStartTime(startTime);
        commitData.setOwner(this.uriUser);
        commitData.setProject(this.project);
        String xmlData = this.makeCommit(commitData);
        logRequest("Commit");
        return super.getStringRepresentation(xmlData);
      }
      catch (Exception e) {
        setStatusError("Error creating Commit DPD.", e);
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
  private String makeCommit(CommitDailyProjectData data) throws Exception {
    JAXBContext codeIssueJAXB = (JAXBContext) this.server.getContext().getAttributes().get(
        "CommitJAXB");
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
