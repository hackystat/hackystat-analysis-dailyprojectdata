package org.hackystat.dailyprojectdata.resource.snapshot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * SensorDataSnapshot provides a generic means to retrieve and return the set of SensorData of a
 * given SDT with the most recent 'runtime' value. It does this by retrieving "buckets" of data for
 * a given interval size (defaulting to 30 minutes), starting at 11:30pm on the given day and
 * working backward until a complete 'snapshot' has been achieved.
 * 
 * The Snapshot algorithm is based upon a number of assumptions that may or may not be true in
 * practice:
 * <ul>
 * <li> When sending data with a given runtime, that "batch" will provide all of the data required
 * for all projects. The snapshot algorithm does not provide any way to "combine" data from multiple
 * runs with different timestamps, each for a separate resource in the project.
 * 
 * <li> This algorithm depends upon the runtime and tstamp fields being "covariant". More precisely,
 * given two SensorData instances for a given day called A and B, if runtime(A) is greater than
 * runtime(B), then tstamp(A) must also be greater than tstamp(B). This assumption enables us to
 * retrieve SensorData by timestamp in reverse chronological order, and stop as soon as we have a
 * complete snapshot of runtime values.
 * 
 * <li> Finally, this Snapshot is based upon the combined SensorData from all users in a Project.
 * Thus, results can be unpredictable when SensorData from multiple users (with overlapping
 * timestamps) is sent at around the same time.
 * </ul>
 * 
 * An alternative algorithm would return a data structure that provides a single entry for every
 * resource found across all of the users, but using only the latest runtime when multiple entries
 * for a single resource exist. The problem with this approach is that if a resource is deleted
 * during a day, it will still be in the snapshot. This can cause bad data when a major refactoring
 * occurs, resulting in many resources being renamed. In this case, many resources will be
 * represented twice in the resulting snapshot--once with their old name and once with their new
 * name.
 * 
 * A potentially helpful extension would be to enable retrieval of a snapshot using only data that
 * contains a property called "DailyProjectDataSnapshot" with a user defined value. This would
 * enable users to define a daily process that creates "complete" versions of project data with a
 * common runtime. This process could run at any time and all other data from other users,
 * regardless of their runtime value, would be ignored.
 * 
 * @author jsakuda
 */
public class SensorDataSnapshot implements Iterable<SensorData> {
  /** If the snapshot has seen older data. */
  private boolean seenOlderData = false;

  /** The bucket size in minutes. */
  private int bucketSize = 30;

  /** The runtime for this snapshot used to determine if older data has been seen. */
  private XMLGregorianCalendar snapshotRuntime = null;

  /** Holds sensor data associated with the latest snapshot. */
  private Set<SensorData> latestSnapshot = new HashSet<SensorData>();

  /** The last bucket that was processed. */
  private SnapshotBucket prevBucket = null;

  /** Start of the snapshot day. */
  private XMLGregorianCalendar startOfDay;

  /** End of the snapshot day. */
  private XMLGregorianCalendar endOfDay;

  /** The number of buckets retrieved to create the snapshot. */
  private int bucketsRetrieved = 0;

  /**
   * Creates a new snapshot.
   * 
   * @param client The <code>SensorBaseClient</code> to be used for querying for
   *          <code>SensorData</code>.
   * @param user The Hackystat user that owns the Project whose data is being retrieved.
   * @param project The project to obtain data for.
   * @param sdt The sensor data type to get data for.
   * @param day The day to get the latest snapshot for.
   * @throws SensorBaseClientException Thrown if there is an error while communication with the
   *           sensorbase server.
   */
  public SensorDataSnapshot(SensorBaseClient client, String user, String project, String sdt,
      Day day) throws SensorBaseClientException {
    this.setUpStartEndDay(day);
    this.createLatestSnapshot(client, user, project, sdt);
  }

  /**
   * Creates a new snapshot.
   * 
   * @param client The <code>SensorBaseClient</code> to be used for querying for
   *          <code>SensorData</code>.
   * @param user The Hackystat user that owns the Project whose data is being retrieved.
   * @param project The project to obtain data for.
   * @param sdt The sensor data type to get data for.
   * @param day The day to get the latest snapshot for.
   * @param bucketSize The interval of time (in minutes) in which data should be retrieved from
   *          the server.
   * @throws SensorBaseClientException Thrown if there is an error while communication with the
   *           sensorbase server.
   */
  public SensorDataSnapshot(SensorBaseClient client, String user, String project, String sdt,
      Day day, int bucketSize) throws SensorBaseClientException {
    this.bucketSize = bucketSize;
    this.setUpStartEndDay(day);
    this.createLatestSnapshot(client, user, project, sdt);
  }

  /**
   * Creates a new snapshot.
   * 
   * @param client The <code>SensorBaseClient</code> to be used for querying for
   *          <code>SensorData</code>.
   * @param user The Hackystat user that owns the Project whose data is being retrieved.
   * @param project The project to obtain data for.
   * @param sdt The sensor data type to get data for.
   * @param day The day to get the latest snapshot for.
   * @param bucketSize The interval of time (in minutes) in which data should be retrieved from
   *          the server.
   * @param tool The tool that data should be retrieved for.
   * @throws SensorBaseClientException Thrown if there is an error while communication with the
   *           sensorbase server.
   */
  public SensorDataSnapshot(SensorBaseClient client, String user, String project, String sdt,
      Day day, int bucketSize, String tool) throws SensorBaseClientException {
    this.bucketSize = bucketSize;
    this.setUpStartEndDay(day);
    this.createLatestToolSnapshot(client, user, project, sdt, tool);
  }

  /**
   * Sets up the start and end day time variables used by the snapshot.
   * 
   * @param day The day to use at when setting the start and end of day.
   */
  private void setUpStartEndDay(Day day) {
    long lastTickOfTheDay = day.getLastTickOfTheDay();
    long firstTickOfTheDay = day.getFirstTickOfTheDay();
    this.startOfDay = Tstamp.makeTimestamp(firstTickOfTheDay);
    this.endOfDay = Tstamp.makeTimestamp(lastTickOfTheDay);
  }

  /**
   * Iterates over intervals of time and queries the server for sensordata to create the latest
   * snapshot for the specified tool.
   * 
   * @param client The <code>SensorBaseClient</code> to be used for querying for
   *          <code>SensorData</code>.
   * @param user The Hackystat user to obtain data for.
   * @param project The project to obtain data for.
   * @param sdt The sensor data type to get data for.
   * @param tool The tool that the snapshot is for.
   * @throws SensorBaseClientException Thrown if there is an error while communication with the
   *           sensorbase server.
   */
  private void createLatestToolSnapshot(SensorBaseClient client, String user, String project,
      String sdt, String tool) throws SensorBaseClientException {

    SnapshotBucket bucket = this.getNextBucket();

    // stop checking if older data is seen, or if
    // bucket becomes null when the entire day has been iterated through
    while (!this.seenOlderData && bucket != null) {
      SensorDataIndex index = client.getProjectSensorData(user, project,
          bucket.getStartTime(), bucket.getEndTime(), sdt);

      List<SensorDataRef> sensorDataRefList = index.getSensorDataRef();
      // sort the list by timestamps, newest first
      Collections.sort(sensorDataRefList, new SensorDataRefComparator(false));
      for (SensorDataRef sensorDataRef : sensorDataRefList) {
        if (this.seenOlderData) {
          // stop iterating, older data found
          break;
        }
        else {
          SensorData sensorData = client.getSensorData(sensorDataRef);
          // check that the data is for the correct tool
          if (sensorData.getTool().equals(tool)) {
            // tool matches, try to add the data
            this.addData(sensorData);
          }
        }
      }
      bucket = this.getNextBucket();
    }

  }

  /**
   * Iterates over intervals of time and queries the server for sensordata to create the
   * snapshot.
   * 
   * @param client The <code>SensorBaseClient</code> to be used for querying for
   *          <code>SensorData</code>.
   * @param user The Hackystat user to obtain data for.
   * @param project The project to obtain data for.
   * @param sdt The sensor data type to get data for.
   * @throws SensorBaseClientException Thrown if there is an error while communication with the
   *           sensorbase server.
   */
  private void createLatestSnapshot(SensorBaseClient client, String user, String project,
      String sdt) throws SensorBaseClientException {

    SnapshotBucket bucket = this.getNextBucket();

    // stop checking if older data is seen, or if
    // bucket becomes null when the entire day has been iterated through
    while (!this.seenOlderData && bucket != null) {
      SensorDataIndex index = client.getProjectSensorData(user, project,
          bucket.getStartTime(), bucket.getEndTime(), sdt);

      List<SensorDataRef> sensorDataRefList = index.getSensorDataRef();
      // sort the list by timestamps, newest first
      Collections.sort(sensorDataRefList, new SensorDataRefComparator(false));
      for (SensorDataRef sensorDataRef : sensorDataRefList) {
        if (this.seenOlderData) {
          // stop iterating, older data found
          break;
        }
        else {
          SensorData sensorData = client.getSensorData(sensorDataRef);
          this.addData(sensorData);
        }
      }
      if (!this.seenOlderData) {
        bucket = this.getNextBucket();
      }
    }
  }

  /**
   * Gets the next bucket of time that should be checked for sensor data.
   * 
   * @return Returns the next snapshot bucket or null if all buckets of time for the given day
   *         have been checked.
   */
  private SnapshotBucket getNextBucket() {
    if (this.prevBucket == null) {
      // return first bucket, which starts at the end of the day
      XMLGregorianCalendar startTime = this.getStartTime(this.endOfDay);
      SnapshotBucket snapshotBucket = new SnapshotBucket(startTime, this.endOfDay);
      this.prevBucket = snapshotBucket;
      this.bucketsRetrieved++;
      return snapshotBucket;
    }
    else if (this.prevBucket.getStartTime().compare(this.startOfDay) == DatatypeConstants.GREATER) {
      // previous bucket did not start at the beginning of the day so,
      // more buckets can still be obtained
      // decrement old start time by 1 millisecont to prevent overlap
      XMLGregorianCalendar newEndTime = Tstamp.incrementMilliseconds(
          this.prevBucket.getStartTime(), -1);
      XMLGregorianCalendar newStartTime = this.getStartTime(newEndTime);

      SnapshotBucket snapshotBucket = new SnapshotBucket(newStartTime, newEndTime);
      this.prevBucket = snapshotBucket;
      this.bucketsRetrieved++;
      return snapshotBucket;
    }
    // previous bucket started at the start of day so it was the last bucket
    return null;
  }

  /**
   * Gets the start time for a bucket based on the end time of the bucket.
   * 
   * @param bucketEnd The end time for the bucket.
   * @return Returns the bucket end time minus the given bucket size or the start of the day if
   *         subtracting the bucket size overshoots the beginning of the day.
   */
  private XMLGregorianCalendar getStartTime(XMLGregorianCalendar bucketEnd) {
    XMLGregorianCalendar startTime = Tstamp.incrementMinutes(bucketEnd, -this.bucketSize);

    if (startTime.compare(this.startOfDay) == DatatypeConstants.LESSER) {
      // calculated start time is before the start of the day
      // set start time to the start of the day so the remaining data is still retrieved
      startTime = this.startOfDay;
    }
    return startTime;
  }

  /**
   * Adds sensor data to the collection of sensor data only if the runtime is valid.
   * 
   * @param data The data to be added to the sensor data collection if it meets all criteria.
   */
  private void addData(SensorData data) {
    XMLGregorianCalendar runtime = data.getRuntime();

    if (this.snapshotRuntime == null) {
      // first entry, use that runtime as the snapshot runtime
      this.snapshotRuntime = runtime;
    }

    if (runtime.compare(this.snapshotRuntime) == DatatypeConstants.LESSER) {
      // new runtime is less than the snapshot runtime, this is older data
      this.seenOlderData = true;
    }
    else {
      this.latestSnapshot.add(data);
    }
  }

  /**
   * Returns an iterator over the last <code>SensorData</code> snapshot.
   * 
   * @return Returns an iterator over the last <code>SensorData</code> snapshot.
   */
  public Iterator<SensorData> iterator() {
    return this.latestSnapshot.iterator();
  }

  /**
   * Returns the Owner responsible for the Snapshot data, or null if there is no Snapshot data.
   * @return The owner, or null. 
   */
  public String getOwner() {
    for (SensorData data : this.latestSnapshot) {
      return data.getOwner();
    }
    return null;
  }

  /**
   * Returns the Tool responsible for the Snapshot data, or null if there is no Snapshot data.
   * @return The owner, or null. 
   */
  public String getTool() {
    for (SensorData data : this.latestSnapshot) {
      return data.getTool();
    }
    return null;
  }
  
  /**
   * Returns true if the Snapshot contains no data. 
   * @return True if the Snapshot contains no data. 
   */
  public boolean isEmpty() {
    return this.latestSnapshot.isEmpty();
  }
  
  /**
   * Gets the number of buckets that were retrieved to create the snapshot. This method is only
   * meant for testing purposes.
   * 
   * @return Returns the number of buckets retrieved during snapshot creation.
   */
  int getNumberOfBucketsRetrieved() {
    return this.bucketsRetrieved;
  }
}
