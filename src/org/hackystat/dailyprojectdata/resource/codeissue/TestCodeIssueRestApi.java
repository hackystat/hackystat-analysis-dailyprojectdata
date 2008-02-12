package org.hackystat.dailyprojectdata.resource.codeissue;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClientException;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueDailyProjectData;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the CodeIssue portion of the DailyProjectData REST API.
 * 
 * @author Philip Johnson, Julie Sakuda.
 */
public class TestCodeIssueRestApi extends DailyProjectDataTestHelper {
  private static final String PREFIX = "Type_";
  private static final String CORRECTNESS_RV = "CORRECTNESS_RV_RETURN_VALUE_IGNORED";
  private static final String LINE_LENGTH = "LineLength";
  private static final String PACKAGE_HTML = "PackageHtml";
  private static final String AVOID_STAR_IMPORT = "AvoidStarImport";
  /** Constant for Project. */
  private static final String PROJECT = "Default";
  /** Constant to make PMD happy. */
  private static final String CHECKSTYLE = "Checkstyle";
  /** The user for this test case. */
  private String user = "TestCodeIssue@hackystat.org";
  
  private  DailyProjectDataClient dpdClient;
  
  private  XMLGregorianCalendar tstamp;
  
  
  /**
   * Create data and send to server if we haven't done it already.
   * @throws Exception If problems occur.
   */
  @Before
  public void setUp () throws Exception {
    // First, create some 'counts' for Checkstyle, FindBugs, and PMD.
    Map<String, Integer> checkstyleCounts = new HashMap<String, Integer>();
    checkstyleCounts.put(AVOID_STAR_IMPORT, 2);
    checkstyleCounts.put(PACKAGE_HTML, 1);
    checkstyleCounts.put(LINE_LENGTH, 2);
    checkstyleCounts.put(CORRECTNESS_RV, 6);
    Map<String, Integer> findbugsCounts = new HashMap<String, Integer>();
    findbugsCounts.put(CORRECTNESS_RV, 8);
    Map<String, Integer> pmdCounts = new HashMap<String, Integer>();

    String runtime = "2007-10-30T02:00:00";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(
        makeCodeIssue("2007-10-30T02:00:00", user, runtime, "PMD", pmdCounts));
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

    // Now connect to the DPD server and make the client.
    dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(), user, user);
    dpdClient.authenticate();
    tstamp = Tstamp.makeTimestamp("2007-10-30");
  }

  /**
   * Test that GET {host}/codeissue/{user}/default/{starttime} works properly with no 
   * type and tool parameters.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testAnyQuery() throws Exception {
    // Test query with no tool or type, so get everything.
    CodeIssueDailyProjectData codeIssue = dpdClient.getCodeIssue(user, PROJECT, tstamp, null, null);
    // There should be 5 entries, one for each tool and type combination.
    assertEquals("Checking for 6 code issue entries.", 6, codeIssue.getCodeIssueData().size());
  }
  
  /**
   * Tests DPD query where type is specified.
   * 
   * @throws DailyProjectDataClientException Thrown if an error occurs.
   */
  @Test
  public void testTypeQuery() throws DailyProjectDataClientException {
    CodeIssueDailyProjectData codeIssue = 
      dpdClient.getCodeIssue(user, PROJECT, tstamp, null, CORRECTNESS_RV);
    List<CodeIssueData> issueList = codeIssue.getCodeIssueData();
    assertEquals("Checking for 2 entries, checkstyle and findbugs.", 2, issueList.size());
    CodeIssueData data = issueList.get(0);
    assertEquals("Tool should be FindBugs.", "FindBugs", data.getTool());
    assertEquals("Issue should be CORRECTNESS_RV.", CORRECTNESS_RV, data.getIssueType());
    assertEquals("Count should be 8", 8, data.getNumIssues());
  }

  /**
   * Tests DPD query with tool and type.
   * 
   * @throws DailyProjectDataClientException Thrown if an error occurs.
   */
  @Test
  public void testToolTypeQuery() throws DailyProjectDataClientException {
    CodeIssueDailyProjectData codeIssue = 
      dpdClient.getCodeIssue(user, PROJECT, tstamp, CHECKSTYLE, AVOID_STAR_IMPORT);
    List<CodeIssueData> issueList = codeIssue.getCodeIssueData();
    assertEquals("Checking for 1 Checkstyle entry.", 1, issueList.size());
  }

  /**
   * Tests DPD query with only tool.
   * 
   * @throws DailyProjectDataClientException Thrown if an error occurs.
   */
  @Test
  public void testToolQuery() throws DailyProjectDataClientException {
    CodeIssueDailyProjectData codeIssue 
    = dpdClient.getCodeIssue(user, PROJECT, tstamp, "PMD", null);
    List<CodeIssueData> issueList = codeIssue.getCodeIssueData();
    assertEquals("Checking for 0 PMD member data instance.", 1, issueList.size());
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
      property.setKey(PREFIX + entry.getKey());
      property.setValue(entry.getValue().toString());
      propertyList.add(property);
    }

    return data;
  }
}
