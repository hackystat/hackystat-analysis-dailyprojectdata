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
public class UnitTestDPDTestHelper {

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
    Properties prop = new Properties();

    // required properties
    Property property = new Property();
    property.setKey("Name");
    property.setValue(testName);
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("Result");
    property.setValue(result);
    prop.getProperty().add(property);

    // optional properties
    property = new Property();
    property.setKey("elapsedTime");
    property.setValue(elapsedTime);
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("testName");
    property.setValue(testName);
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("testCaseName");
    property.setValue(testCaseName);
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("failureString");
    property.setValue(failureString);
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("errorString");
    property.setValue(errorString);
    prop.getProperty().add(property);

    data.setProperties(prop);

    return data;
  }

  /**
   * Creates a sample SensorData UnitTest instance given a timestamp and a user.
   *
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @return The new SensorData DevEvent instance.
   * @throws Exception If problems occur.
   */
  public SensorData makeUnitTestEvent(String tstampString, String user) throws Exception {
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

    // test count, test time, success, failure
    Properties prop = new Properties();

    // required properties
    Property property = new Property();
    property.setKey("Name");
    property.setValue("testName");
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("Result");
    property.setValue("failure");
    prop.getProperty().add(property);

    // optional properties
    property = new Property();
    property.setKey("elapsedTime");
    property.setValue("15");
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("testName");
    property.setValue("org.hackystat.core.installer.util.TestProxyProperty");
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("testCaseName");
    property.setValue("testNormalFunctionality");
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("failureString");
    property.setValue("Value should be the same. expected:<[8]0> but was:<[9]0>");
    prop.getProperty().add(property);

    property = new Property();
    property.setKey("errorString");
    property.setValue("Value of error string");
    prop.getProperty().add(property);

    data.setProperties(prop);

    return data;
  }

}
