package org.hackystat.dailyprojectdata.resource.coverage;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
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
   * Returns the data that has the specified owner and granularity. If no such
   * data exists, an empty list is returned.
   * @param owner the owner of the data.
   * @param granularity the coverage granularity of the data. (line, class,
   * method, etc).
   * @return the list of coverage data associated with the owner and coverage
   * granularity.
   */
  public List<CoverageData> getData(String owner, String granularity) {
    List<CoverageData> filtereddata = new ArrayList<CoverageData>();
    for (CoverageData data : this.data) {
      Property property = data.getCoverageProperty("Granularity");
      if (data.getOwner().equalsIgnoreCase(owner)
          && property.getValue().equalsIgnoreCase(granularity)) {
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
