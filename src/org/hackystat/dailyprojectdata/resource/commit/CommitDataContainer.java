package org.hackystat.dailyprojectdata.resource.commit;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * The data container that abstracts the data retrieval of Commit information.
 * @author aito
 * 
 */
public class CommitDataContainer {
  /** The list of commit data. */
  private List<CommitData> data = new ArrayList<CommitData>();

  /**
   * Adds the specified SensorData instance to this container.
   * @param data the specified data instance.
   */
  public void addCommitData(SensorData data) {
    this.data.add(new CommitData(data));
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
    for (CommitData data : this.getDataWithOwner(owner)) {
      totalLinesAdded += data.getLinesAdded();
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
    for (CommitData data : this.getDataWithOwner(owner)) {
      totalLinesDeleted += data.getLinesDeleted();
    }
    return totalLinesDeleted;
  }

  /**
   * Returns the total lines changed by the specified owner.
   * @param owner the specified owner.
   * @return the total lines changed.
   */
  public int getLinesChanged(String owner) {
    int totalLinesChanged = 0;
    for (CommitData data : this.getDataWithOwner(owner)) {
      totalLinesChanged += data.getLinesChanged();
    }
    return totalLinesChanged;
  }

  /**
   * Returns the total commits made by the specified owner.
   * @param owner the specified owner.
   * @return the total commits.
   */
  public int getCommits(String owner) {
    return this.getDataWithOwner(owner).size();
  }

  /**
   * Returns a list of all of the Commit data associated with the specified
   * owner.
   * @param owner the specified owner.
   * @return the list of commit data.
   */
  private List<CommitData> getDataWithOwner(String owner) {
    List<CommitData> dataWithOwner = new ArrayList<CommitData>();
    for (CommitData data : this.data) {
      if (data.getOwner().equals(owner)) {
        dataWithOwner.add(data);
      }
    }
    return dataWithOwner;
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
