package org.hackystat.dailyprojectdata.resource.commit;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * The data container that abstracts the data retrieval of Commit information.
 * 
 * To Do: (1) change from a List to a Map[Owner, SensorData]. 
 *   
 * @author aito
 * 
 */
public class CommitDataContainer {
  
  /** The list of commit data. */
  private List<CommitData> data = new ArrayList<CommitData>();

  /**
   * Adds the specified SensorData instance to this container if it contains the
   * three Commit properties (linesDeleted, linesAdded, totalLines) and if these property
   * values are integers. 
   * @param data the specified data instance.
   */
  public void addCommitData(SensorData data) {
    if (isValidCommitData(data)) {
      this.data.add(new CommitData(data));
    }
  }
  
  /**
   * Returns true if the SensorData instance has the Commit properties, false otherwise.
   * @param data The sensor data instance. 
   * @return True if the sensor data instance is a Commit instance. 
   */
  private boolean isValidCommitData(SensorData data) {
    try {
      Integer.valueOf(this.getProperty(data, "linesDeleted"));
      Integer.valueOf(this.getProperty(data, "linesAdded"));
    }
    catch (Exception e) {
      return false;
    }
    return true;
  }
  
  /**
   * Returns the String value associated with property key.
   * If no such property exists, returns null.
   * 
   * @param data The sensor data instance.
   * @param propertyName the property name to search for.
   * @return The property value, or null.
   */
  private String getProperty(SensorData data, String propertyName) {
    List<Property> propertyList = data.getProperties().getProperty();
    for (Property property : propertyList) {
      if (propertyName.equals(property.getKey())) {
        return property.getValue();
      }
    }
    return null;
  }

  /**
   * Returns a set of owners of the wrapped SensorData.
   * @return the list of sensor data owners.
   */
  public List<String> getOwners() {
    List<String> owners = new ArrayList<String>();
    for (CommitData data : this.data) {
      if (!owners.contains(data.getOwner())) {
        owners.add(data.getOwner());
      }
    }
    return owners;
  }

  /**
   * Returns the total lines added by the specified owner.
   * @param owner the specified owner.
   * @return the total lines added.
   */
  public int getLinesAdded(String owner) {
    int totalLinesAdded = 0;
    for (CommitData data : this.data) {
      if (data.getOwner().equals(owner)) {
        totalLinesAdded += data.getLinesAdded();
      }
    }
    return totalLinesAdded;
  }

  /**
   * Returns the total lines deleted by the specified owner.
   * @param owner the specified owner.
   * @return the total lines deleted.
   */
  public int getLinesDeleted(String owner) {
    int totalLinesDeleted = 0;
    for (CommitData data : this.data) {
      if (data.getOwner().equals(owner)) {
        totalLinesDeleted += data.getLinesDeleted();
      }
    }
    return totalLinesDeleted;
  }


  /**
   * Returns the total commits made by the specified owner.
   * @param owner the specified owner.
   * @return the total commits.
   */
  public int getCommits(String owner) {
    int numCommits = 0;
    for (CommitData data : this.data) {
      if (data.getOwner().equals(owner)) {
        numCommits++;
      }
    }
    return numCommits;
  }

  /**
   * Returns a copy of the list containing all of the added SensorData
   * instances.
   * @return the SensorData list copy.
   */
  public List<CommitData> getData() {
    return new ArrayList<CommitData>(this.data);
  }
}
