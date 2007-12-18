package org.hackystat.dailyprojectdata.resource.coverage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * The class wrapping all of SensorData entries that are related. This class
 * enforces no rules on how each SensorData instance is related. A normal usage
 * of this class is store data with the same runtime. stored in each container.
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

  /**
   * Returns the runtime of the data stored in this container. This method
   * assumes that all of the data stored in this container have the same
   * runtime. If no data is stored in this container, a runtime exception is
   * thrown because there is no runtime.
   * @return the runtime of the data stored in this container.
   */
  public XMLGregorianCalendar getRuntime() {
    if (this.data.isEmpty()) {
      throw new IllegalStateException("No data stored in this container");
    }
    return this.data.get(0).getRuntime();
  }
}
