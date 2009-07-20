package org.hackystat.dailyprojectdata.resource.issue;
/*
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;*/
import org.hackystat.dailyprojectdata.resource.dailyprojectdata.DailyProjectDataResource;
//import org.hackystat.dailyprojectdata.resource.issue.jaxb.IssueDailyProjectData;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
//import org.w3c.dom.Document;

/**
 * Implements the Resource for processing GET {host}/issue/{user}/{project}/{starttime} requests.
 * Requires the authenticated user to be {user} or else the Admin user for the sensorbase 
 * connected to this service. 
 * @author Shaoxuan Zhang
 */
public class IssueResource extends DailyProjectDataResource {

  /**
   * The standard constructor.
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public IssueResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns an IssueDailyProjectData instance representing the Issue associated with the 
   * Project data, or null if not authorized. 
   * Authenticated user must be the uriUser, or Admin, or project member. 
   * @param variant The representational variant requested.
   * @return The representation. 
   */
  @Override
  public Representation represent(Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Returns the passed SensorData instance as a String encoding of its XML representation.
   * @param data The SensorData instance. 
   * @return The XML String representation.
   * @throws Exception If problems occur during translation. 
   *//*
  private String makeDevTime (IssueDailyProjectData data) throws Exception {
    JAXBContext devTimeJAXB = 
      (JAXBContext)this.server.getContext().getAttributes().get("IssueJAXB");
    Marshaller marshaller = devTimeJAXB.createMarshaller(); 
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
  }*/
}
