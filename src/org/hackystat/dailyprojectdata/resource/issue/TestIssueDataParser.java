package org.hackystat.dailyprojectdata.resource.issue;

import static org.junit.Assert.assertEquals;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.dailyprojectdata.resource.issue.jaxb.IssueData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Test IssueDataParser.
 * @author Shaoxuan Zhang
 *
 */
public class TestIssueDataParser {

  private IssueDataParser parser = new IssueDataParser(Logger.getLogger("IssueDpdTester"));
  
  protected static final String ACCEPTED = "Accepted";
  protected static final String FIXED = "Fixed";
  protected static final String DEFECT = "Defect";
  protected static final String ENHANCEMENT = "Enhancement";
  protected static final String MEDIUM = "Medium";
  protected static final String HIGH = "High";

  protected static final String testDataOwner = "testDataOwner@hackystat.org";
  protected static final String testUser1 = "tester1@hackystat.org";
  protected static final String testUser2 = "tester2@hackystat.org";
  protected static final String testProject = "hackystat-test-project";
  
  protected static final String[] columnKeyOrder = {
      IssueDataParser.TYPE_PROPERTY_KEY, IssueDataParser.STATUS_PROPERTY_KEY, 
      IssueDataParser.PRIORITY_PROPERTY_KEY, IssueDataParser.MILESTONE_PROPERTY_KEY, 
      IssueDataParser.OWNER_PROPERTY_KEY};
  protected static final String[] testData1T1 = 
      {"21", DEFECT, ACCEPTED, MEDIUM, "", testUser1, "2008-09-07T11:00:00"};
  protected static final String[] testData1T2 = 
      {"21", ENHANCEMENT, FIXED, MEDIUM, "8.4", testUser1, "2008-09-07T11:00:00"};
  protected static final String[] testData2T1 = 
      {"23", ENHANCEMENT, ACCEPTED, MEDIUM, "", testUser1, "2009-07-20T10:24:06"};
  protected static final String[] testData2T2 = 
      {"23", ENHANCEMENT, ACCEPTED, HIGH, "", testUser2, "2009-07-20T10:24:06"};
  protected final XMLGregorianCalendar testTime1;
  protected final XMLGregorianCalendar testTime2;
  
  private final SensorData testData1;
  private final SensorData testData2;
  
  /**
   * Constructor, prepare the test data.
   * @throws Exception if error when making XMLGregorianCalendar timestamps.
   */
  public TestIssueDataParser() throws Exception {
    testTime1 = Tstamp.makeTimestamp("2009-07-20T11:00:00");
    testTime2 = Tstamp.makeTimestamp("2009-07-22T00:00:00");
    testData1 = makeIssueSensorData(testDataOwner, new String[][]{testData1T1, testData1T2}, 
        new XMLGregorianCalendar[]{testTime1, testTime2});
    testData2 = makeIssueSensorData(testDataOwner, new String[][]{testData2T1, testData2T2}, 
        new XMLGregorianCalendar[]{testTime1, testTime2});
  }

  /**
   * Test IsOpenStatus().
   */
  @Test public void testIsOpenStatus() {
    String[] openStatusValues = {"New", "Accepted", "Started"};
    String[] closeStatusValues = {"Fixed", "Verified", "Invalid", "Duplicate", "WontFix", "Done"};
    for (String openStatus : openStatusValues) {
      assertEquals(openStatus + " should be an open status.", 
          true, parser.isOpenStatus(openStatus));
    }
    for (String closeStatus : closeStatusValues) {
      assertEquals(closeStatus + " should be an close status.", 
          false, parser.isOpenStatus(closeStatus));
    }
  }
  
  /**
   * Test parser to parse issue sensordata.
   */
  @Test public void testParser() {
    /*
    System.out.println("testSensorData1");
    for (Property p : testSensorData1.getProperties().getProperty()) {
      System.out.println("  " + p.getKey() + " : " + p.getValue());
    }
    */
    IssueData issueData1T1 = parser.getIssueDpd(testData1, testTime1);
    IssueData issueData1T2 = parser.getIssueDpd(testData1, testTime2);
    IssueData issueData2T1 = parser.getIssueDpd(testData2, testTime1);
    IssueData issueData2T2 = parser.getIssueDpd(testData2, testTime2);
    assertEquals("Compare id of data1 from two time.", issueData1T1.getId(), issueData1T2.getId());
    assertEquals("Compare id of data2 to predefined value.", 23, issueData2T1.getId());
    
    assertEquals("Testing type of data1 when 1st update.", DEFECT, issueData1T1.getType());
    assertEquals("Testing type of data1 when 2nd update.", ENHANCEMENT, issueData1T2.getType());
    
    assertEquals("Testing status of data1 when 1st update.", ACCEPTED, issueData1T1.getStatus());
    assertEquals("Testing status of data1 when 2nd update.", FIXED, issueData1T2.getStatus());
    
    assertEquals("Testing milestone of data1 when 1st update.", null, issueData1T1.getMilestone());
    assertEquals("Testing milestone of data1 when 2nd update.", "8.4", issueData1T2.getMilestone());

    assertEquals("Testing status of data2 when 1st update.", ACCEPTED, issueData2T1.getStatus());
    assertEquals("Testing status of data2 when 2nd update.", ACCEPTED, issueData2T2.getStatus());
    
    assertEquals("Testing priority of data2 when 1st update.", MEDIUM, issueData2T1.getPriority());
    assertEquals("Testing priority of data2 when 2nd update.", HIGH, issueData2T2.getPriority());
    
    assertEquals("Testing owner of data2 when 1st update.", testUser1, issueData2T1.getOwner());
    assertEquals("Testing owner of data2 when 2nd update.", testUser2, issueData2T2.getOwner());
  }


  /**
   * Make the issue sensordata. 
   * Example input parameters:
   * strings = {{"21", "Enhancement", "Accepted", "Medium", "", "rlcox0"},
                {"21", "Enhancement", "Fixed", "High", "8.4", "rlcox0"}}
     timestamps = {"2009-07-20T11:00:00", "2009-07-22T00:00:00"}
     @param dataOwner owner of the data.
   * @param strings Array of String array which contain state values in order.
   * @param timestamps Array of timestamp of each String array in 1st parameter.
   * @return the sensordata.
   */
  public static SensorData makeIssueSensorData(String dataOwner, String[][] strings,
      XMLGregorianCalendar[] timestamps) {
    String sdt = "Issue";
    String tool = "GoogleProjectHosting";
    
    SensorData sensorData = new SensorData();
    sensorData.setTool(tool);
    sensorData.setOwner(dataOwner);
    sensorData.setSensorDataType(sdt);
    try {
      sensorData.setTimestamp(Tstamp.makeTimestamp(strings[0][6]));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    String id = strings[0][0];
    sensorData.setResource("http://code.google.com/p/" + testProject + "/issues/detail?id=" + id);
    sensorData.setRuntime(Tstamp.makeTimestamp());
    //add id
    sensorData.addProperty(IssueDataParser.ID_PROPERTY_KEY, id);
    for (int i = 0; i < strings.length; i++) {
      String[] values = strings[i];
      String timeString = timestamps[i].toString();
      //add other state values.
      for (int j = 0; j < columnKeyOrder.length; j++) {
        if (values[j + 1].length() > 0) {
          sensorData.addProperty(columnKeyOrder[j], 
              values[j + 1] + IssueDataParser.TIMESTAMP_SEPARATOR + timeString);
        }
      }
    }
    return sensorData;
  }
}
