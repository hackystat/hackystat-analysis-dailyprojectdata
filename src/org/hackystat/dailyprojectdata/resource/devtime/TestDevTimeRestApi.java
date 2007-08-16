package org.hackystat.dailyprojectdata.resource.devtime;

import static org.junit.Assert.assertEquals;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.devtime.jaxb.DevTimeDailyProjectData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the DevTime part of the DailyProjectData REST API. 
 * @author Philip Johnson
 */
public class TestDevTimeRestApi extends DailyProjectDataTestHelper {
  
  /** The user for this test case. */
  private String user = "TestDevTime@hackystat.org";
  
  /**
   * Test that GET {host}/devtime/{user}/default/{starttime} works properly.
   * First, it creates a test user and sends some sample DevEvent data to the SensorBase. 
   * Then, it invokes the GET request and checks to see that it obtains the right answer. 
   * Finally, it deletes the data and the user. 
   * @throws Exception If problems occur.
   */
  @Test public void getDefaultDevTime() throws Exception {
    // First, create a batch of DevEvent sensor data.
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(makeDevEvent("2007-04-30T02:00:00", user));
    batchData.getSensorData().add(makeDevEvent("2007-04-30T02:10:00", user));
    batchData.getSensorData().add(makeDevEvent("2007-04-29T23:55:00", user));
    batchData.getSensorData().add(makeDevEvent("2007-05-01T00:01:00", user));
    
    // Connect to the sensorbase and register the DailyProjectDataDevEvent user. 
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
    client.authenticate();
    // Send the sensor data to the SensorBase. 
    client.putSensorDataBatch(batchData);
    
    // Now connect to the DPD server. 
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(), 
        user, user);
    dpdClient.authenticate(); 
    DevTimeDailyProjectData devTime = dpdClient.getDevTime(user, "default", 
        Tstamp.makeTimestamp("2007-04-30"));
    assertEquals("Checking default devTime", 0, devTime.getTotalDevTime().intValue());
  }
  
  /**
   * Creates a sample SensorData DevEvent instance given a timestamp and a user. 
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @return The new SensorData DevEvent instance.
   * @throws Exception If problems occur. 
   */
  private SensorData makeDevEvent(String tstampString, String user) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    String sdt = "DevEvent";
    SensorData data = new SensorData();
    String tool = "Emacs";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(tstamp);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(tstamp);
    Property property = new Property();
    property.setKey("Type");
    property.setValue("StateChange");
    Properties properties = new Properties();
    properties.getProperty().add(property);
    data.setProperties(properties);
    return data;
  }

}
