package org.hackystat.dailyprojectdata.resource.coupling;

import static org.junit.Assert.assertEquals;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.coupling.jaxb.CouplingDailyProjectData;
import org.hackystat.dailyprojectdata.test.DailyProjectDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the Coupling DPD.
 *  
 * @author Philip Johnson
 */
public class TestCouplingRestApi extends DailyProjectDataTestHelper {

  /** The user for this test case. */
  private String user = "TestCoupling@hackystat.org";
  
  /** Used to guarantee tstamp uniqueness. */
  private int counter = 0;

  /**
   * Test that GET {host}/coupling/{user}/{project}/{starttime}/{type}?Tool={tool} works properly.
   * First, it creates a test user and sends some sample Coupling data to the
   * SensorBase. Then, it invokes the GET request and checks to see that it
   * obtains the right answer. Finally, it deletes the data and the user.
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void getCoupling() throws Exception {
    // First, create a batch of Coupling sensor data.
    SensorDatas batchData = new SensorDatas();
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp("2007-04-30T02:00:00");
    String afferent1 = "1";
    String efferent1 = "2";
    String afferent2 = "3";
    String efferent2 = "4";
    batchData.getSensorData().add(makeFileMetric(tstamp, afferent1, efferent1));
    batchData.getSensorData().add(makeFileMetric(tstamp, afferent2, efferent2));
    
    // Connect to the sensorbase and register the test user.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
    client.authenticate();
    // Send the sensor data to the SensorBase.
    client.putSensorDataBatch(batchData);

    // Now connect to the DPD server.
    DailyProjectDataClient dpdClient = new DailyProjectDataClient(getDailyProjectDataHostName(),
        user, user);
    dpdClient.authenticate();
    CouplingDailyProjectData coupling = 
      dpdClient.getCoupling(user, "Default", tstamp, "package", "JDepend");
    assertEquals("Checking two entries returned", 2, coupling.getCouplingData().size());
    assertEquals("Checking 1", 1, coupling.getCouplingData().get(0).getAfferent().intValue());
    assertEquals("Checking 2", 2, coupling.getCouplingData().get(0).getEfferent().intValue());
    assertEquals("Checking 3", 3, coupling.getCouplingData().get(1).getAfferent().intValue());
    assertEquals("Checking 4", 4, coupling.getCouplingData().get(1).getEfferent().intValue());
  }

  /**
   * Creates a sample SensorData FileMetric instance given a timestamp and a list of coupling
   * values.
   *
   * @param tstamp The timestamp, used as the Runtime and auto-incremented for the tstamp.
   * @param afferent The afferent coupling value.
   * @param efferent The efferent coupling value.
   * @return The new SensorData Coupling instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeFileMetric(XMLGregorianCalendar tstamp, String afferent, String efferent) 
  throws Exception {
    String sdt = "Coupling";
    SensorData data = new SensorData();
    String tool = "JDepend";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(Tstamp.incrementMinutes(tstamp, counter++));
    data.setRuntime(tstamp);
    data.setResource("/users/johnson/Foo-" + counter);
    Property property = new Property();
    property.setKey("Afferent");
    property.setValue(afferent);
    Property property2 = new Property();
    property2.setKey("Efferent");
    property2.setValue(efferent);
    Properties properties = new Properties();
    properties.getProperty().add(property);
    properties.getProperty().add(property2);
    data.setProperties(properties);
    return data;
  }

}
