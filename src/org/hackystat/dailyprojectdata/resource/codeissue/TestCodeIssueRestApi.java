package org.hackystat.dailyprojectdata.resource.codeissue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClientException;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueDailyProjectData;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.MemberData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the CodeIssue portion of the DailyProjectData REST API.
 * 
 * @author jsakuda
 */
public class TestCodeIssueRestApi extends DailyProjectDataTestHelper {
  private static final String TYPE_CORRECTNESS_RV = "Type_CORRECTNESS_RV_RETURN_VALUE_IGNORED";

  private static final String TYPE_LINE_LENGTH = "Type_LineLength";

  private static final String TYPE_PACKAGE_HTML = "Type_PackageHtml";

  private static final String TYPE_AVOID_STAR_IMPORT = "Type_AvoidStarImport";

  /** Constant for Project. */
  private static final String PROJECT = "Default";

  /** Constant to make PMD happy. */
  private static final String CHECKSTYLE = "Checkstyle";

  /** The user for this test case. */
  private String user = "TestCodeIssue@hackystat.org";

  /**
   * Test that GET {host}/codeissue/{user}/default/{starttime} works properly. First, it
   * creates a test user and sends some sample CodeIssue data to the SensorBase. Then, it
   * invokes the GET request and checks to see that it obtains the right answer. Finally, it
   * deletes the data and the user.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testGetCodeIssues() throws Exception {
    // First, create a batch of DevEvent sensor data.

    Map<String, Integer> checkstyleCounts = new HashMap<String, Integer>();
    checkstyleCounts.put(TYPE_AVOID_STAR_IMPORT, 2);
    checkstyleCounts.put(TYPE_PACKAGE_HTML, 1);
    checkstyleCounts.put(TYPE_LINE_LENGTH, 2);

    Map<String, Integer> findbugsCounts = new HashMap<String, Integer>();
    findbugsCounts.put(TYPE_CORRECTNESS_RV, 6);

    String runtime = "2007-10-30T02:00:00";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:00:00", user, runtime, "PMD",
            new HashMap<String, Integer>()));
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:10:00", user, runtime, CHECKSTYLE, checkstyleCounts));
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:15:00", user, runtime, "FindBugs", findbugsCounts));

    // Connect to the sensorbase and register the DailyProjectDataCodeIssue user.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
    client.authenticate();
    // Send the sensor data to the SensorBase.
    client.putSensorDataBatch(batchData);

    // Now connect to the DPD server.
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(
        getDailyProjectDataHostName(), user, user);
    dpdClient.authenticate();

    XMLGregorianCalendar requestTstamp = Tstamp.makeTimestamp("2007-10-30");

    // Test query with no tool or type
    CodeIssueDailyProjectData codeIssue = dpdClient.getCodeIssue(user, PROJECT, requestTstamp,
        null, null);
    // There should be 3 member data entries because there are 3 tools
    assertSame("Checking for 3 code issue entries.", 3, codeIssue.getMemberData().size());

    testToolQuery(dpdClient, requestTstamp);

    testToolTypeQuery(dpdClient, requestTstamp);

    testTypeQuery(dpdClient, requestTstamp);

    // First, delete all sensor data sent by this user.
    SensorDataIndex index = client.getSensorDataIndex(user);
    for (SensorDataRef ref : index.getSensorDataRef()) {
      client.deleteSensorData(user, ref.getTimestamp());
    }
    // Now delete the user too.
    client.deleteUser(user);
  }

  /**
   * Tests DPD query with only type.
   * 
   * @param dpdClient The client to use to query.
   * @param requestTstamp The timestamp to retrieve test data.
   * @throws DailyProjectDataClientException Thrown if an error occurs.
   */
  private void testTypeQuery(DailyProjectDataClient dpdClient,
      XMLGregorianCalendar requestTstamp) throws DailyProjectDataClientException {
    CodeIssueDailyProjectData codeIssue;
    List<MemberData> memberDatas;
    MemberData memberData;
    codeIssue = dpdClient
        .getCodeIssue(user, PROJECT, requestTstamp, null, TYPE_CORRECTNESS_RV);
    memberDatas = codeIssue.getMemberData();
    assertSame("Checking for 1 FindBugs member data instance.", 1, memberDatas.size());
    memberData = memberDatas.get(0);
    assertEquals("Tool should be FindBugs.", "FindBugs", memberData.getTool());
    Map<QName, String> otherAttributes2 = memberData.getOtherAttributes();
    assertSame("Should have only one extra attribute.", 1, otherAttributes2.size());
    String count = otherAttributes2.get(otherAttributes2.keySet().iterator().next());
    assertSame("Should have count of 6 for Type_CORRECTNESS_RV.", 6, Integer.valueOf(count)
        .intValue());
  }

  /**
   * Tests DPD query with tool and type.
   * 
   * @param dpdClient The client to use to query.
   * @param requestTstamp The timestamp to retrieve test data.
   * @throws DailyProjectDataClientException Thrown if an error occurs.
   */
  private void testToolTypeQuery(DailyProjectDataClient dpdClient,
      XMLGregorianCalendar requestTstamp) throws DailyProjectDataClientException {
    CodeIssueDailyProjectData codeIssue;
    List<MemberData> memberDatas;
    MemberData memberData;
    codeIssue = dpdClient.getCodeIssue(user, PROJECT, requestTstamp, CHECKSTYLE,
        TYPE_AVOID_STAR_IMPORT);
    memberDatas = codeIssue.getMemberData();
    assertSame("Checking for 1 Checkstyle member data instance.", 1, memberDatas.size());
    memberData = memberDatas.get(0);
    Map<QName, String> otherAttributes = memberData.getOtherAttributes();
    assertSame("Other attributes should have 3 mappings.", 3, otherAttributes.size());
    for (Entry<QName, String> entry : otherAttributes.entrySet()) {
      QName qName = entry.getKey();
      if (TYPE_AVOID_STAR_IMPORT.equals(qName.getLocalPart())) {
        String countString = entry.getValue();
        assertSame("Should have count of 2 for Type_AvoidStarImport.", 2, Integer.valueOf(
            countString).intValue());
      }
      else if (TYPE_PACKAGE_HTML.equals(qName.getLocalPart())) {
        String countString = entry.getValue();
        assertSame("Should have count of 1 for Type_PackageHtml.", 1, Integer.valueOf(
            countString).intValue());
      }
      else if (TYPE_LINE_LENGTH.equals(qName.getLocalPart())) {
        String countString = entry.getValue();
        assertSame("Should have count of 2 for Type_LineCount.", 2, Integer.valueOf(
            countString).intValue());
      }
    }
  }

  /**
   * Tests DPD query with only tool.
   * 
   * @param dpdClient The client to use to query.
   * @param requestTstamp The timestamp to retrieve test data.
   * @throws DailyProjectDataClientException Thrown if an error occurs.
   */
  private void testToolQuery(DailyProjectDataClient dpdClient,
      XMLGregorianCalendar requestTstamp) throws DailyProjectDataClientException {
    CodeIssueDailyProjectData codeIssue;
    codeIssue = dpdClient.getCodeIssue(user, PROJECT, requestTstamp, "PMD", null);
    List<MemberData> memberDatas = codeIssue.getMemberData();
    assertSame("Checking for 1 PMD member data instance.", 1, memberDatas.size());
    MemberData memberData = memberDatas.get(0);
    // PMD has zero data so there should be no Type: attributes available.
    assertTrue("PMD MemberData should have no other attributes.", memberData
        .getOtherAttributes().isEmpty());
  }

  /**
   * Creates a sample SensorData CodeIssue instance.
   * 
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @param runtimeString The runtime.
   * @param tool The tool.
   * @param typeCounts A mapping of types for the given tool to the number of occurrences of
   *          that error type.
   * @return The new SensorData CodeIssue instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeCodeIssue(String tstampString, String user, String runtimeString,
      String tool, Map<String, Integer> typeCounts) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(runtimeString);
    SensorData data = new SensorData();
    data.setSensorDataType("CodeIssue");
    data.setOwner(user);
    data.setTimestamp(tstamp);
    data.setTool(tool);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(runtime);

    data.setProperties(new Properties());
    Properties properties = data.getProperties();
    List<Property> propertyList = properties.getProperty();
    for (Entry<String, Integer> entry : typeCounts.entrySet()) {
      Property property = new Property();
      property.setKey(entry.getKey());
      property.setValue(entry.getValue().toString());
      propertyList.add(property);
    }

    return data;
  }
}
