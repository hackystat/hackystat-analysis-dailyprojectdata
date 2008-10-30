package org.hackystat.dailyprojectdata.resource.build;

import static org.hackystat.dailyprojectdata.server.ServerProperties.SENSORBASE_FULLHOST_KEY;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
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

import org.hackystat.dailyprojectdata.resource.build.jaxb.BuildDailyProjectData;
import org.hackystat.dailyprojectdata.resource.build.jaxb.MemberData;
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
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
 * Implements the Resource for processing GET {host}/build/{user}/{project}/{timestamp}
 * requests. 
 * Authenticated user must be the uriUser, or Admin, or project member. 
 * 
 * @author Philip Johnson
 */
public class BuildResource extends DailyProjectDataResource {

  /** The optional type. */
  private String type;

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public BuildResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.type = (String) request.getAttributes().get("Type");
  }

  /**
   * Returns a BuildDailyProjectData instance representing the build totals associated with the
   * Project data, or null if not authorized.
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    Logger logger = this.server.getLogger();
    logger.fine("Build DPD: Starting");
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        // [2] Check the front side cache and return if the DPD is found and is OK to access.
        String cachedDpd = this.server.getFrontSideCache().get(uriUser, uriString);
        if (cachedDpd != null && client.inProject(authUser, project)) {
          return super.getStringRepresentation(cachedDpd);
        }
        // [2] get a SensorDataIndex of all Build data for this Project on the requested day.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        logger.fine("Build DPD: Requesting index: " + uriUser + " " + project);
        SensorDataIndex index = null;
        index = client.getProjectSensorData(uriUser, project, startTime,
            endTime, "Build");
        logger.fine("Build DPD: Got index: " + index.getSensorDataRef().size() + " instances");
        // [3] update the build data counter
        MemberBuildCounter counter = new MemberBuildCounter();
        List<SensorDataRef> sensorDataRefList = index.getSensorDataRef();
        for (SensorDataRef sensorDataRef : sensorDataRefList) {
          SensorData data = client.getSensorData(sensorDataRef);
          String result = this.getPropertyValue(data, "Result");
          boolean valid = this.isValidData(data);
          if (valid && "Success".equals(result)) {
            counter.addSuccessfulBuild(data.getOwner());
          }
          else if (valid && "Failure".equals(result)) {
            counter.addFailedBuild(data.getOwner());
          }
        }
        logger.fine("Build DPD: retrieved all instances, now building the DPD.");
        // [4] create and return the BuildDailyProjectData
        BuildDailyProjectData build = new BuildDailyProjectData();
        String sensorBaseHost = this.server.getServerProperties().get(SENSORBASE_FULLHOST_KEY);
        Map<String, Integer> successfulBuilds = counter.getSuccessfulBuilds();
        Map<String, Integer> failedBuilds = counter.getFailedBuilds();

        Set<String> members = counter.getMembers();
        for (String member : members) {
          MemberData memberData = new MemberData();
          memberData.setMemberUri(sensorBaseHost + "users/" + member);
          Integer failures = failedBuilds.get(member);
          if (failures == null) {
            // no mapping for member in failed builds, means user had no failed builds
            failures = 0;
          }
          memberData.setFailure(failures);

          Integer successes = successfulBuilds.get(member);
          if (successes == null) {
            // no mapping for member in successful builds, means user had no successful builds
            successes = 0;
          }
          memberData.setSuccess(successes);

          build.getMemberData().add(memberData);
        }
        
        build.setOwner(uriUser);
        build.setProject(project);
        build.setStartTime(startTime);
        if (this.type == null) {
          build.setType("*");
        }
        else {
          build.setType(this.type);
        }
        
        String xmlData = this.makeBuild(build);
        this.server.getFrontSideCache().put(uriUser, uriString, xmlData);
        logRequest("Build", this.type);
        return super.getStringRepresentation(xmlData);
      }
      catch (Exception e) {
        setStatusError("Error creating Build DPD", e);
        return null;
      }
    }
    return null;
  }

  /**
   * Determines if the sensor data matches the specified Type.
   * 
   * @param data The sensor data to check for a valid Type.
   * @return Returns true if the data should be processed or false if it does not match the
   *         Type.
   */
  private boolean isValidData(SensorData data) {
    // no type, or Type=* are wildcards for all data
    if (this.type == null || "*".equals(this.type)) {
      return true;
    }

    String typeValue = getPropertyValue(data, "Type");
    return this.type.equals(typeValue);
  }

  /**
   * Gets the value for the given property name from the <code>Properties</code> object
   * contained in the given sensor data instance.
   * 
   * @param data The sensor data instance to get the property from.
   * @param propertyName The name of the property to get the value for.
   * @return Returns the value of the property or null if no matching property was found.
   */
  private String getPropertyValue(SensorData data, String propertyName) {
    Properties properties = data.getProperties();
    if (properties != null) {
      List<Property> propertyList = properties.getProperty();
      for (Property property : propertyList) {
        if (property.getKey().equals(propertyName)) {
          return property.getValue();
        }
      }
    }
    return null;
  }
  
  /**
   * Returns the passed SensorData instance as a String encoding of its XML representation.
   * 
   * @param data The SensorData instance.
   * @return The XML String representation.
   * @throws Exception If problems occur during translation.
   */
  private String makeBuild(BuildDailyProjectData data) throws Exception {
    JAXBContext buildJAXB = (JAXBContext) this.server.getContext().getAttributes().get(
        "BuildJAXB");
    Marshaller marshaller = buildJAXB.createMarshaller();
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
