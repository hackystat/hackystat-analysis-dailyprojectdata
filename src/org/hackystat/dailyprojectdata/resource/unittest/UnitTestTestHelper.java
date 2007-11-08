package org.hackystat.dailyprojectdata.resource.unittest;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Used by the tests in UnitTestDPD module. Encapsulates some common code between tests.
 *
 * @author Pavel Senin.
 *
 */
public class UnitTestTestHelper {

  /**
   * /** Creates a sample SensorData UnitTest instance given a whole set of parameters.
   *
   * @param tstampString The timestamp as a string
   * @param user The user email.
   * @param resource The resource over which test was run over.
   * @param testName The unit test name.
   * @param result The unit test result.
   * @param elapsedTime The unit test elapsed time.
   * @param testCaseName The unit test case name.
   * @param failureString The unit test failure string.
   * @param errorString The unit test error string.
   * @return The new SensorData DevEvent instance.
   * @throws Exception If problems occur.
   */
  public SensorData makeUnitTestEvent(String tstampString, String user, String resource,
      String testName, String result, String elapsedTime, String testCaseName,
      String failureString, String errorString) throws Exception {

    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    String sdt = "UnitTest";
    SensorData data = new SensorData();
    String tool = "JUnit";

    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(tstamp);
    data.setResource(resource);
    data.setRuntime(tstamp);

    // test count, test time, success, failure
    Properties properties = new Properties();
    addProperty(properties, "Name", testName);
    addProperty(properties, "Result", result);
    addProperty(properties, "ElapsedTime", elapsedTime);
    addProperty(properties, "TestName", testName);
    addProperty(properties, "TestCaseName", testCaseName);
    addProperty(properties, "FailureString", failureString);
    addProperty(properties, "ErrorString", errorString);
    data.setProperties(properties);

    return data;
  }

  /**
   * Creates a sample passing SensorData UnitTest instance given a timestamp and a user.
   *
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @param pass True if passing, false if failing.
   * @return The new SensorData DevEvent instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeUnitTestEvent(String tstampString, String user, boolean pass) 
  throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    String sdt = "UnitTest";
    SensorData data = new SensorData();
    String tool = "JUnit";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(tstamp);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(tstamp);

    Properties prop = new Properties();
    addProperty(prop, "Name", "testName");
    addProperty(prop, "Result", (pass ? "pass" : "fail"));
    addProperty(prop, "ElapsedTime", "15");
    addProperty(prop, "TestName", "org.Foo");
    addProperty(prop, "TestCaseName", "testFoo");
    data.setProperties(prop);
    return data;
  }
  
  /**
   * Returns a Unit Test instance with Result = pass.
   * @param tstampString The timestamp.
   * @param user The user. 
   * @return The UnitTest sensor data instance. 
   * @throws Exception If problems occur.
   */
  public SensorData makePassUnitTest(String tstampString, String user) throws Exception {
    return makeUnitTestEvent(tstampString, user, true);  
  }
  
  /**
   * Returns a Unit Test instance with Result = fail.
   * @param tstampString The timestamp.
   * @param user The user. 
   * @return The UnitTest sensor data instance. 
   * @throws Exception If problems occur.
   */
  public SensorData makeFailUnitTest(String tstampString, String user) throws Exception {
    return makeUnitTestEvent(tstampString, user, false);  
  }
  
  /**
   * Adds the key value pair to the Properties object.
   * @param properties The Properties.
   * @param key The key. 
   * @param value The value. 
   */
  private void addProperty(Properties properties, String key, String value) {
    Property property = new Property();
    property.setKey(key);
    property.setValue(value);
    properties.getProperty().add(property);
  }

}
