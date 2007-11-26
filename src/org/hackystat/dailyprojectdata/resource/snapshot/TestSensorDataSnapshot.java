package org.hackystat.dailyprojectdata.resource.snapshot;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.sensorbase.server.Server;
import org.hackystat.sensorbase.server.ServerProperties;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the functionality of the <code>SensorDataSnapshot</code>.
 * 
 * @author jsakuda
 */
public class TestSensorDataSnapshot {

  private static final String CHECKSTYLE = "Checkstyle";

  private static final String PMD = "PMD";
  
  /** The test user. */
  private static String user = "TestSnapshot@hackystat.org";
  private static String host;
  private static Server server;

  /**
   * Starts the server going for these tests, and makes sure our test user is registered.
   * 
   * @throws Exception If problems occur setting up the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
    ServerProperties properties = new ServerProperties();
    properties.setTestProperties();
    TestSensorDataSnapshot.server = Server.newInstance(properties);
    TestSensorDataSnapshot.host = TestSensorDataSnapshot.server.getHostName();
    SensorBaseClient.registerUser(host, user);

    addSensorData();
  }

  /**
   * Gets rid of the sent sensor data and the user.
   * 
   * @throws Exception If problems occur setting up the server.
   */
  @AfterClass
  public static void teardownServer() throws Exception {
    // Now delete all data sent by this user.
    SensorBaseClient client = new SensorBaseClient(host, user, user);
    // First, delete all sensor data sent by this user.
    SensorDataIndex index = client.getSensorDataIndex(user);
    for (SensorDataRef ref : index.getSensorDataRef()) {
      client.deleteSensorData(user, ref.getTimestamp());
    }
    // Now delete the user too.
    client.deleteUser(user);
  }

  /**
   * Tests the getting of the last snapshot of the day.
   * 
   * @throws Exception Thrown if there is a failure to communicate with the sensorbase.
   */
  @Test
  public void testSnapshot() throws Exception {
    SensorBaseClient client = new SensorBaseClient(host, user, user);
    Day day = Day.getInstance("30-Oct-2007");

    SensorDataSnapshot snapshot = new SensorDataSnapshot(client, user, "Default", "CodeIssue",
        day, 30);
    Iterator<SensorData> iterator = snapshot.iterator();

    int dataCount = 0;
    while (iterator.hasNext()) {
      SensorData sensorData = iterator.next();
      assertEquals("Checking tool.", PMD, sensorData.getTool());
      dataCount++;
    }

    assertEquals("Should have 4 data entries.", 4, dataCount);
  }

  /**
   * Test the getting of the last snapshot of the day for a particular tool.
   * 
   * @throws Exception Thrown if there is a failure communicating with the sensorbase.
   */
  @Test
  public void testSnapshotWithTool() throws Exception {
    SensorBaseClient client = new SensorBaseClient(host, user, user);
    Day day = Day.getInstance("30-Oct-2007");

    SensorDataSnapshot snapshot = new SensorDataSnapshot(client, user, "Default", "CodeIssue",
        day, 30, CHECKSTYLE);
    Iterator<SensorData> iterator = snapshot.iterator();

    int dataCount = 0;
    while (iterator.hasNext()) {
      SensorData sensorData = iterator.next();
      assertEquals("Checking tool.", CHECKSTYLE, sensorData.getTool());
      dataCount++;
    }

    assertEquals("Should have 3 data entries.", 3, dataCount);
  }

  /**
   * Adds test sensor data to the sensor base for testing snapshots.
   * 
   * @throws Exception Thrown if there are errors with the sensorbase.
   */
  private static void addSensorData() throws Exception {
    SensorBaseClient client = new SensorBaseClient(host, user, user);
    client.authenticate();

    String runtime = "2007-10-30T02:00:00";
    SensorDatas batchData = new SensorDatas();
    batchData.getSensorData().add(makeSensorData("2007-10-30T02:00:00", user, runtime, PMD));
    batchData.getSensorData().add(makeSensorData("2007-10-30T02:05:00", user, runtime, PMD));

    client.putSensorDataBatch(batchData);

    String runtime2 = "2007-10-30T04:00:00";
    batchData = new SensorDatas();
    batchData.getSensorData().add(
        makeSensorData("2007-10-30T04:00:00", user, runtime2, CHECKSTYLE));
    batchData.getSensorData().add(
        makeSensorData("2007-10-30T04:15:00", user, runtime2, CHECKSTYLE));
    batchData.getSensorData().add(
        makeSensorData("2007-10-30T04:35:00", user, runtime2, CHECKSTYLE));

    client.putSensorDataBatch(batchData);

    String runtime3 = "2007-10-30T06:00:00";
    batchData = new SensorDatas();
    batchData.getSensorData()
        .add(makeSensorData("2007-10-30T06:00:00", user, runtime3, PMD));
    batchData.getSensorData()
        .add(makeSensorData("2007-10-30T06:05:00", user, runtime3, PMD));
    batchData.getSensorData()
        .add(makeSensorData("2007-10-30T06:10:00", user, runtime3, PMD));
    batchData.getSensorData()
        .add(makeSensorData("2007-10-30T06:20:00", user, runtime3, PMD));

    client.putSensorDataBatch(batchData);
  }

  /**
   * Makes a fake sensor data to be used for testing.
   * 
   * @param tstampString The timestamp of the data.
   * @param user The user for the data.
   * @param runtimeString The batch runtime.
   * @param tool The tool for the data.
   * @return Returns the new sensor data instance.
   * @throws Exception Thrown if errors occur.
   */
  private static SensorData makeSensorData(String tstampString, String user,
      String runtimeString, String tool) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    XMLGregorianCalendar runtime = Tstamp.makeTimestamp(runtimeString);
    SensorData data = new SensorData();
    data.setSensorDataType("CodeIssue");
    data.setOwner(user);
    data.setTimestamp(tstamp);
    data.setTool(tool);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(runtime);

    return data;
  }
}
