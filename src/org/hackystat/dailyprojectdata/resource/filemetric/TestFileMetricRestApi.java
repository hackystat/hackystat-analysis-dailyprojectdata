package org.hackystat.dailyprojectdata.resource.filemetric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.filemetric.jaxb.FileMetricDailyProjectData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the FileMetric DPD. 
 * @author Philip Johnson
 */
public class TestFileMetricRestApi extends DailyProjectDataTestHelper {

  /** The user for this test case. */
  private String user = "TestFileMetric@hackystat.org";
  
  /** Used to guarantee tstamp uniqueness. */
  private int counter = 0;

  /**
   * Test that GET {host}/filemetric/{user}/default/{starttime}/TotalLines works properly.
   * First, it creates a test user and sends some sample FileMetric data to the
   * SensorBase. Then, it invokes the GET request and checks to see that it
   * obtains the right answer. Finally, it deletes the data and the user.
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void getDefaultFileMetric() throws Exception {
    // First, create a batch of DevEvent sensor data.
    SensorDatas batchData = new SensorDatas();
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp("2007-04-30T02:00:00");
    batchData.getSensorData().add(makeFileMetric(tstamp, 111));
    batchData.getSensorData().add(makeFileMetric(tstamp, 89));
    
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
    String size = "TotalLines";
    FileMetricDailyProjectData fileMetric = dpdClient.getFileMetric(user, "Default", tstamp, size);
    assertEquals("Checking default size", 200, fileMetric.getTotal(), 0.01);
    fileMetric = dpdClient.getFileMetric(user, "Default", Tstamp.makeTimestamp("2007-05-01"), size);
    assertTrue("Checking empty data", fileMetric.getFileData().isEmpty());
  }

  /**
   * Creates a sample SensorData FileMetric instance given a timestamp and a size.
   *
   * @param tstamp The timestamp, used as the Runtime and auto-incremented for the tstamp.
   * @param size The size used as the value of the TotalLines property.
   * @return The new SensorData FileMetric instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeFileMetric(XMLGregorianCalendar tstamp, int size) throws Exception {
    String sdt = "FileMetric";
    SensorData data = new SensorData();
    String tool = "SCLC";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(Tstamp.incrementMinutes(tstamp, counter++));
    data.setRuntime(tstamp);
    data.setResource("/users/johnson/Foo-" + counter + ".java");
    Property property = new Property();
    property.setKey("TotalLines");
    property.setValue(String.valueOf(size));
    Properties properties = new Properties();
    properties.getProperty().add(property);
    data.setProperties(properties);
    return data;
  }

}
