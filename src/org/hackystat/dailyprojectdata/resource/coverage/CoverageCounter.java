package org.hackystat.dailyprojectdata.resource.coverage;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * The class managing the the latest snapshot of coverage data.
 * 
 * @author aito
 * 
 */
public class CoverageCounter {
  /** The current latest runtime. */
  private long latestRuntime = 0L;
  /** The object storing the latest snapshot of coverage data. */
  private CoverageDataContainer dataContainer = new CoverageDataContainer();

  /**
   * Adds the specified data instance to this class.
   * @param data the specified data instance.
   */
  public void addCoverageData(SensorData data) {
    long runtime = Tstamp.makeTimestamp(data.getRuntime()).getTime();

    if (runtime > this.latestRuntime) {
      this.latestRuntime = runtime;
      this.dataContainer = new CoverageDataContainer();
    }
    this.dataContainer.addCoverageData(data);
  }

  /**
   * Returns the last runtime with sensor data.
   * @return the last runtime value.
   */
  public XMLGregorianCalendar getLastRuntime() {
    if (this.latestRuntime == 0L) {
      return null;
    }
    return Tstamp.makeTimestamp(this.latestRuntime);
  }

  /**
   * Returns the last batch of coverage data stored in this class.
   * @return the last batch of data.
   */
  public CoverageDataContainer getLatestBatch() {
    return this.dataContainer;
  }
}
