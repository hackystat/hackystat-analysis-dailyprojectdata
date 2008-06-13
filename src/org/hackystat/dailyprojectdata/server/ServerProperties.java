package org.hackystat.dailyprojectdata.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Provides access to the values stored in the dailyprojectdata.properties file. 
 * @author Philip Johnson
 */
public class ServerProperties {
  
  /** The sensorbase fully qualified host name, such as http://localhost:9876/sensorbase. */
  public static final String SENSORBASE_FULLHOST_KEY =  "dailyprojectdata.sensorbase.host";
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
  /** The Restlet Logging key. */
  public static final String RESTLET_LOGGING_KEY = "dailyprojectdata.restlet.logging";
  /** The dpd port key during testing. */
  public static final String TEST_PORT_KEY =       "dailyprojectdata.test.port";
  /** The test installation key. */
  public static final String TEST_INSTALL_KEY =    "dailyprojectdata.test.install";
  /** The test installation key. */
  public static final String TEST_HOSTNAME_KEY =    "dailyprojectdata.test.hostname";
  /** The test installation key. */
  public static final String TEST_SENSORBASE_FULLHOST_KEY = "dailyprojectdata.test.sensorbase.host";
  /** Indicates whether SensorBaseClient caching is enabled. */
  public static final String CACHE_ENABLED = "dailyprojectdata.cache.enabled";
  /** The maxLife in days for each instance in each SensorBaseClient cache. */
  public static final String CACHE_MAX_LIFE = "dailyprojectdata.cache.max.life";
  /** The total capacity of each SensorBaseClient cache. */
  public static final String CACHE_CAPACITY = "dailyprojectdata.cache.capacity";

  /** Where we store the properties. */
  private Properties properties; 
 
  /**
   * Creates a new ServerProperties instance. 
   * Prints an error to the console if problems occur on loading. 
   */
  public ServerProperties() {
    try {
      initializeProperties();
    }
    catch (Exception e) {
      System.out.println("Error initializing server properties: " + e.getMessage());
    }
  }
  
  /**
   * Reads in the properties in ~/.hackystat/dailyprojectdata/dailyprojectdata.properties 
   * if this file exists, and provides default values for all properties.
   * @throws Exception if errors occur.
   */
  private void initializeProperties () throws Exception {
    String userHome = System.getProperty("user.home");
    String userDir = System.getProperty("user.dir");
    String propFile = userHome + "/.hackystat/dailyprojectdata/dailyprojectdata.properties";
    this.properties = new Properties();
    // Set defaults
    properties.setProperty(SENSORBASE_FULLHOST_KEY, "http://localhost:9876/sensorbase/");
    properties.setProperty(HOSTNAME_KEY, "localhost");
    properties.setProperty(PORT_KEY, "9877");
    properties.setProperty(CONTEXT_ROOT_KEY, "dailyprojectdata");
    properties.setProperty(LOGGING_LEVEL_KEY, "INFO");
    properties.setProperty(RESTLET_LOGGING_KEY, "false");
    properties.setProperty(XML_DIR_KEY, userDir + "/xml");
    properties.setProperty(TEST_PORT_KEY, "9977");
    properties.setProperty(TEST_HOSTNAME_KEY, "localhost");
    properties.setProperty(TEST_SENSORBASE_FULLHOST_KEY, "http://localhost:9976/sensorbase");
    properties.setProperty(TEST_INSTALL_KEY, "false");
    properties.setProperty(CACHE_ENABLED, "true");
    properties.setProperty(CACHE_MAX_LIFE, "365");
    properties.setProperty(CACHE_CAPACITY, "500000");
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(propFile);
      System.out.println("Loading DailyProjectData properties from: " + propFile);
      properties.load(stream);
    }
    catch (IOException e) {
      System.out.println(propFile + " not found. Using default dailyprojectdata properties.");
    }
    finally {
      if (stream != null) {
        stream.close();
      }
    }
    
    trimProperties(properties);
    // make sure that SENSORBASE_HOST always has a final slash.
    String sensorBaseHost = (String) properties.get(SENSORBASE_FULLHOST_KEY);
    if (!sensorBaseHost.endsWith("/")) {
      properties.put(SENSORBASE_FULLHOST_KEY, sensorBaseHost + "/");
    }
  }
  
  /**
   * Sets the following properties' values to their "test" equivalent.
   * <ul>
   * <li> HOSTNAME_KEY
   * <li> PORT_KEY
   * <li> SENSORBASE_FULLHOST_KEY
   * </ul>
   * Also sets TEST_INSTALL_KEY's value to "true".
   */
  public void setTestProperties() {
    properties.setProperty(HOSTNAME_KEY, properties.getProperty(TEST_HOSTNAME_KEY));
    properties.setProperty(PORT_KEY, properties.getProperty(TEST_PORT_KEY));
    properties.setProperty(SENSORBASE_FULLHOST_KEY, 
    properties.getProperty(TEST_SENSORBASE_FULLHOST_KEY));
    properties.setProperty(TEST_INSTALL_KEY, "true");
    properties.setProperty(CACHE_ENABLED, "false");
    trimProperties(properties);
  }

  /**
   * Returns a string indicating current property settings. 
   * @return The string with current property settings. 
   */
  public String echoProperties() {
    String cr = System.getProperty("line.separator"); 
    String eq = " = ";
    String pad = "                ";
    return "DailyProjectData Properties:" + cr +
      pad + SENSORBASE_FULLHOST_KEY   + eq + get(SENSORBASE_FULLHOST_KEY) + cr +
      pad + HOSTNAME_KEY      + eq + get(HOSTNAME_KEY) + cr +
      pad + CONTEXT_ROOT_KEY  + eq + get(CONTEXT_ROOT_KEY) + cr +
      pad + PORT_KEY          + eq + get(PORT_KEY) + cr +
      pad + LOGGING_LEVEL_KEY + eq + get(LOGGING_LEVEL_KEY) + cr +
      pad + TEST_INSTALL_KEY + eq + get(TEST_INSTALL_KEY) + cr +
      pad + CACHE_ENABLED + eq + get(CACHE_ENABLED) + cr +
      pad + CACHE_MAX_LIFE + eq + get(CACHE_MAX_LIFE) + cr +
      pad + CACHE_CAPACITY + eq + get(CACHE_CAPACITY);
  }
  
  /**
   * Returns the value of the Server Property specified by the key.
   * @param key Should be one of the public static final strings in this class.
   * @return The value of the key, or null if not found.
   */
  public String get(String key) {
    return this.properties.getProperty(key);
  }
  
  /**
   * Ensures that the there is no leading or trailing whitespace in the property values.
   * The fact that we need to do this indicates a bug in Java's Properties implementation to me. 
   * @param properties The properties. 
   */
  private void trimProperties(Properties properties) {
    // Have to do this iteration in a Java 5 compatible manner. no stringPropertyNames().
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      String propName = (String)entry.getKey();
      properties.setProperty(propName, properties.getProperty(propName).trim());
    }
  }
  
  /**
   * Returns the fully qualified host name, such as "http://localhost:9877/dailyprojectdata/".
   * @return The fully qualified host name.
   */
  public String getFullHost() {
    return "http://" + get(HOSTNAME_KEY) + ":" + get(PORT_KEY) + "/" + get(CONTEXT_ROOT_KEY) + "/";
  }
  
  /**
   * Returns true if caching is enabled in this service. 
   * @return True if caching enabled.
   */
  public boolean isCacheEnabled() {
    return Boolean.valueOf(this.properties.getProperty(CACHE_ENABLED));
  }
  
  /**
   * Returns the caching max life as a double.
   * If the property has an illegal value, then return the default. 
   * @return The max life of each instance in the cache.
   */
  public double getCacheMaxLife() {
    String maxLifeString = this.properties.getProperty(CACHE_MAX_LIFE);
    double maxLife = 0;
    try {
      maxLife = Double.valueOf(maxLifeString);
    }
    catch (Exception e) {
      System.out.println("Illegal cache max life: " + maxLifeString + ". Using default.");
      maxLife = 365D;
    }
    return maxLife;
  }
  
  /**
   * Returns the in-memory capacity for each cache.
   * If the property has an illegal value, then return the default. 
   * @return The in-memory capacity.
   */
  public long getCacheCapacity() {
    String capacityString = this.properties.getProperty(CACHE_CAPACITY);
    long capacity = 0;
    try {
      capacity = Long.valueOf(capacityString);
    }
    catch (Exception e) {
      System.out.println("Illegal cache capacity: " + capacityString + ". Using default.");
      capacity = 500000L;
    }
    return capacity;
  }

}
