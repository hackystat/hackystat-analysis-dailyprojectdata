package org.hackystat.dailyprojectdata.resource.complexity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.complexity.jaxb.ComplexityDailyProjectData;
import org.hackystat.dailyprojectdata.resource.complexity.jaxb.FileData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the Complexity DPD.
 *  
 * @author Philip Johnson
 */
public class TestComplexityRestApi extends DailyProjectDataTestHelper {

  /** The user for this test case. */
  private String user = "TestComplexity@hackystat.org";
  
  /** Used to guarantee tstamp uniqueness. */
  private int counter = 0;

  /**
   * Test that GET {host}/complexity/{user}/{project}/{starttime}/{type}?Tool={tool} works properly.
   * First, it creates a test user and sends some sample FileMetric data to the
   * SensorBase. Then, it invokes the GET request and checks to see that it
   * obtains the right answer. Finally, it deletes the data and the user.
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void getComplexity() throws Exception {
    // First, create a batch of DevEvent sensor data.
    SensorDatas batchData = new SensorDatas();
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp("2007-04-30T02:00:00");
    String complexityList1 = "1, 2";
    String complexityList2 = "3, 4";
    String totalLines1 = "300";
    String totalLines2 = "200";
    batchData.getSensorData().add(makeFileMetric(tstamp, complexityList1, totalLines1));
    batchData.getSensorData().add(makeFileMetric(tstamp, complexityList2, totalLines2));
    
    // Connect to the sensorbase and register the test user.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
    client.authenticate();
    // Send the FileMetric sensor data to the SensorBase.
    client.putSensorDataBatch(batchData);

    // Now connect to the DPD server.
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(),
        user, user);
    dpdClient.authenticate();
    ComplexityDailyProjectData complexity = 
      dpdClient.getComplexity(user, "Default", tstamp, "Cyclomatic", "JavaNCSS");
    assertEquals("Checking two entries returned", 2, complexity.getFileData().size());
    Set<String> complexitySet = new HashSet<String>();
    Set<String> totalLinesSet = new HashSet<String>();
    for (FileData data : complexity.getFileData()) {
      complexitySet.add(data.getComplexityValues());
      totalLinesSet.add(data.getTotalLines());
    }
    assertTrue("Checking complexity 1", complexitySet.contains(complexityList1));
    assertTrue("Checking complexity 2", complexitySet.contains(complexityList2));
    assertTrue("Checking totalLines 1", totalLinesSet.contains(totalLines1));
    assertTrue("Checking totalLines 2", totalLinesSet.contains(totalLines2));
  }

  /**
   * Creates a sample SensorData FileMetric instance given a timestamp and a list of complexity
   * values.
   *
   * @param tstamp The timestamp, used as the Runtime and auto-incremented for the tstamp.
   * @param values The list of complexity data values.
   * @param totalLines The total LOC for this file used to compute the complexity data. 
   * @return The new SensorData FileMetric instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeFileMetric(XMLGregorianCalendar tstamp, String values, String totalLines) 
  throws Exception {
    String sdt = "FileMetric";
    SensorData data = new SensorData();
    String tool = "JavaNCSS";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(Tstamp.incrementMinutes(tstamp, counter++));
    data.setRuntime(tstamp);
    data.setResource("/users/johnson/Foo-" + counter + ".java");
    Property property = new Property();
    property.setKey("CyclomaticComplexityList");
    property.setValue(values);
    Property property2 = new Property();
    property2.setKey("TotalLines");
    property2.setValue(totalLines);
    Properties properties = new Properties();
    properties.getProperty().add(property);
    properties.getProperty().add(property2);
    data.setProperties(properties);
    return data;
  }

}
