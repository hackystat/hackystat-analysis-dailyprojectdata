package org.hackystat.dailyprojectdata.resource.codeissue;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Provides a mechanism for generating a "snapshot" for each of several tools. 
 * The "snapshot" is the set of sensor data instances for the given tool with the 
 * latest runtime value. 
 * 
 * To use this tool, create an instance, then add() each sensor data instance of interest
 * to it. When finished, you can obtain a snapshot for any given tool, which is the set
 * of sensor data instances associated with that tool with the latest runtime value.
 * 
 * @author Philip Johnson
 *
 */
public class MultiToolSnapshot {
  
  /** Holds a map from tool to a set containing the latest snapshot of sensor data for this tool.*/
  private Map<String, Set<SensorData>> tool2SensorData = new HashMap<String, Set<SensorData>>();

  /**
   * Create a MultiToolSnapshot.
   */
  public MultiToolSnapshot() {
    // don't do anything here.
  }
  
  /**
   * Processes the SensorData instance, potentially adding it if its runtime is equal to or greater
   * than the runtime associated with the stored instances of that tool.
   * @param data The sensor data instance. 
   */
  public void add(SensorData data) {
    String tool = data.getTool();
    // If our data structure does not already have an entry for tool, make one.
    if (tool2SensorData.get(tool) == null) {
      tool2SensorData.put(tool, new HashSet<SensorData>());
    }
    
    // If we don't have any stored sensor data for this tool, then add it and return.
    if (tool2SensorData.get(tool).isEmpty()) {
      tool2SensorData.get(tool).add(data);
      return;
    }
    
    // Otherwise we have stored sensor data for this tool.
    XMLGregorianCalendar newRuntime = data.getRuntime();
    XMLGregorianCalendar storedRuntime = getStoredRuntime(tool);

    // Case 1: new runtime equals stored runtime -> add it. 
    if (Tstamp.equal(newRuntime, storedRuntime)) {
      tool2SensorData.get(tool).add(data);
    }
    // Case 2: new runtime is more recent -> replace with it.
    else if (Tstamp.greaterThan(newRuntime, storedRuntime)) {
      tool2SensorData.get(tool).clear();
      tool2SensorData.get(tool).add(data);
    }
    // Case 3: new runtime is older, so don't do anything.
  }
  
  /**
   * Returns the set of all Tools in this MultiToolSnapshot.
   * @return The set of all tools.
   */
  public Set<String> getTools() {
    return tool2SensorData.keySet();
  }

  /**
   * Returns the latest snapshot associated with the specified tool.
   * @param tool The tool name.
   * @return The snapshot, which could be empty.
   */
  public Set<SensorData> getSensorData(String tool) {
    Set<SensorData> snapshot = tool2SensorData.get(tool);
    return (snapshot == null) ? new HashSet<SensorData>() : snapshot;
  }
  
  
  
  /**
   * Returns the stored runtime associated with Tool.
   * @param tool The tool whose stored runtime should be returned.
   * @return The stored runtime, or null if no entries in the map.
   */
  private XMLGregorianCalendar getStoredRuntime(String tool) {
    Set<SensorData> dataSet = tool2SensorData.get(tool);
    for (SensorData data : dataSet) {
      return data.getRuntime();
    }
    return null;
  }

}
