package org.hackystat.dailyprojectdata.resource.codeissue;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * Sorts code issue sensor data into groups by their runtime.
 * 
 * @author jsakuda
 *
 */
public class CodeIssueRuntimeSorter {
  /** Mapping of Runtime (as XMLGregorianCalendar) to all SensorData with the Runtime. */
  private SortedMap<XMLGregorianCalendar, List<SensorData>> runtimeToSensorDataMap;

  /** Initializes the sorter. */
  public CodeIssueRuntimeSorter() {
    this.runtimeToSensorDataMap = new TreeMap<XMLGregorianCalendar, List<SensorData>>(
        new TimestampComparator());
  }

  /**
   * Adds a CodeIssue SensorData to be sorted.
   * 
   * @param sensorData The code issue data to be added.
   */
  public void addCodeIssueData(SensorData sensorData) {
    // sanity check
    if (!sensorData.getSensorDataType().equals("CodeIssue")) {
      return;
    }
    this.put(sensorData.getRuntime(), sensorData);
  }
  
  /**
   * Puts the sensor data in the mapping using its runtime.
   * 
   * @param runtime The runtime associated with the sensor data.
   * @param sensorData The sensor data instance.
   */
  private void put(XMLGregorianCalendar runtime, SensorData sensorData) {
    List<SensorData> sensorDataList;
    if (this.runtimeToSensorDataMap.containsKey(runtime)) {
      sensorDataList = this.runtimeToSensorDataMap.get(runtime);
    }
    else {
      sensorDataList = new ArrayList<SensorData>();
      this.runtimeToSensorDataMap.put(runtime, sensorDataList);
    }
    sensorDataList.add(sensorData);
  }

  /**
   * Gets the collection of CodeIssue sensor data with the latest runtime.
   * 
   * @return Returns the collection of data with the latest runtime.
   */
  public List<SensorData> getLastCodeIssueBatch() {
    if (this.runtimeToSensorDataMap.keySet().isEmpty()) {
      // no data, just return an empty list
      return new ArrayList<SensorData>();
    }
    XMLGregorianCalendar lastKey = this.runtimeToSensorDataMap.lastKey();
    return this.runtimeToSensorDataMap.get(lastKey);
  }
  
  /**
   * Gets the latest runtime for all the data.
   * 
   * @return Returns the latest runtime.
   */
  public XMLGregorianCalendar getLastRuntime() {
    if (this.runtimeToSensorDataMap.keySet().isEmpty()) {
      // no data just return null
      return null;
    }
    return this.runtimeToSensorDataMap.lastKey();
  }
}
