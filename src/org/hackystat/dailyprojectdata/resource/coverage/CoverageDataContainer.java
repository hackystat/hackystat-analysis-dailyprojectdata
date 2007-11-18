package org.hackystat.dailyprojectdata.resource.coverage;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * The class wrapping all of SensorData entries that are related. This class
 * enforces no rules on how each SensorData instance is related. The classes
 * that implement this container should specify the contract of what data is
 * stored in each container.
 * @author aito
 * 
 */
public class CoverageDataContainer {
  /** The list of coverage data. */
  private List<CoverageData> data = new ArrayList<CoverageData>();

  /**
   * Adds the specified SensorData instance to this container.
   * @param data the specified data instance.
   */
  public void addCoverageData(SensorData data) {
    this.data.add(new CoverageData(data));
  }

  /**
   * Returns a set of owners of the wrapped SensorData.
   * @return the list of sensor data owners.
   */
  public List<String> getOwners() {
    List<String> owners = new ArrayList<String>();
    for (CoverageData data : this.data) {
      if (!owners.contains(data.getOwner())) {
        owners.add(data.getOwner());
      }
    }
    return owners;
  }

  /**
   * Returns the data that has the specified owner. If no such data exists, an
   * empty list is returned.
   * @param owner the owner of the data.
   * @return the list of coverage data associated with the owner and coverage
   * granularity.
   */
  public List<CoverageData> getData(String owner) {
    List<CoverageData> filtereddata = new ArrayList<CoverageData>();
    for (CoverageData data : this.data) {
      if (data.getOwner().equalsIgnoreCase(owner)) {
        filtereddata.add(data);
      }
    }
    return filtereddata;
  }

  /**
   * Returns a copy of the list containing all of the added SensorData
   * instances.
   * @return the SensorData list copy.
   */
  public List<CoverageData> getData() {
    return new ArrayList<CoverageData>(this.data);
  }
}
