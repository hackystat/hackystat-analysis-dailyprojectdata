package org.hackystat.dailyprojectdata.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Provides access to the values stored in the dailyprojectdata.properties file. 
 * @author Philip Johnson
 */
public class ServerProperties {
  
  /** The sensorbase host. */
  public static final String SENSORBASE_HOST_KEY =  "dailyprojectdata.sensorbase.host";
  /** The dailyprojectdata hostname key. */
  public static final String HOSTNAME_KEY =        "dailyprojectdata.hostname";
  /** The dailyprojectdata context root. */
  public static final String CONTEXT_ROOT_KEY =     "dailyprojectdata.context.root";
  /** The logging level key. */
  public static final String LOGGING_LEVEL_KEY =   "dailyprojectdata.logging.level";
  /** The dailyprojectdata port key. */
  public static final String PORT_KEY =            "dailyprojectdata.port";
  /** The XML directory key. */
  public static final String XML_DIR_KEY =         "dailyprojectdata.xml.dir";
  
  /**
   * Reads in the properties in ~/.hackystat/dailyprojectdata/dailyprojectdata.properties 
   * if this file exists, and provides default values for all properties. .
   * @throws Exception if errors occur.
   */
  static void initializeProperties () throws Exception {
    String userHome = System.getProperty("user.home");
    String userDir = System.getProperty("user.dir");
    String propFile = userHome + "/.hackystat/dailyprojectdata/dailyprojectdata.properties";
    Properties properties = new Properties();
    // Set defaults
    properties.setProperty(SENSORBASE_HOST_KEY, "http://localhost:9876/sensorbase/");
    properties.setProperty(HOSTNAME_KEY, "localhost");
    properties.setProperty(PORT_KEY, "9877");
    properties.setProperty(CONTEXT_ROOT_KEY, "dailyprojectdata");
    properties.setProperty(LOGGING_LEVEL_KEY, "INFO");
    properties.setProperty(XML_DIR_KEY, userDir + "/xml");
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(propFile);
      properties.load(stream);
      System.out.println("Loading DailyProjectData properties from: " + propFile);
    }
    catch (IOException e) {
      System.out.println(propFile + " not found. Using default dailyprojectdata properties.");
    }
    finally {
      if (stream != null) {
        stream.close();
      }
    }
    // Now add to System properties.
    Properties systemProperties = System.getProperties();
    systemProperties.putAll(properties);
    System.setProperties(systemProperties);
  }

  /**
   * Prints all of the sensorbase settings to the logger.
   * @param server The SensorBase server.   
   */
  static void echoProperties(Server server) {
    String cr = System.getProperty("line.separator"); 
    String eq = " = ";
    String pad = "                ";
    String propertyInfo = "SensorBase Properties:" + cr +
      pad + SENSORBASE_HOST_KEY   + eq + get(SENSORBASE_HOST_KEY) + cr +
      pad + HOSTNAME_KEY      + eq + get(HOSTNAME_KEY) + cr +
      pad + CONTEXT_ROOT_KEY  + eq + get(CONTEXT_ROOT_KEY) + cr +
      pad + LOGGING_LEVEL_KEY + eq + get(LOGGING_LEVEL_KEY) + cr +
      pad + PORT_KEY          + eq + get(PORT_KEY) + cr +
      pad + XML_DIR_KEY       + eq + get(XML_DIR_KEY);
    server.getLogger().info(propertyInfo);
  }
  
  /**
   * Returns the value of the Server Property specified by the key.
   * @param key Should be one of the public static final strings in this class.
   * @return The value of the key, or null if not found.
   */
  public static String get(String key) {
    return System.getProperty(key);
  }
  
  /**
   * Returns the fully qualified host name, such as "http://localhost:9877/dailyprojectdata/".
   * @return The fully qualified host name.
   */
  public static String getFullHost() {
    return "http://" + get(HOSTNAME_KEY) + ":" + get(PORT_KEY) + "/" + get(CONTEXT_ROOT_KEY) + "/";
  }
}
