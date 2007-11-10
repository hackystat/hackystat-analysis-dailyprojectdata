package org.hackystat.dailyprojectdata.resource.coverage;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.resource.codeissue.TimestampComparator;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * The class managing the mapping of coverage runtimes -> coverage data. The
 * runtime is used to associate Coverage SensorData together.
 * 
 * @author aito
 * 
 */
public class CoverageCounter {
  /** The mapping of data runtime to the tool coverage data. */
  private SortedMap<XMLGregorianCalendar, CoverageDataContainer> timeToCoverage = 
    new TreeMap<XMLGregorianCalendar, CoverageDataContainer>(new TimestampComparator());

  /**
   * Adds the specified data instance to this class.
   * @param data the specified data instance.
   */
  public void addCoverageData(SensorData data) {
    XMLGregorianCalendar runtime = data.getRuntime();
    if (this.timeToCoverage.containsKey(runtime)) {
      CoverageDataContainer coverageData = this.timeToCoverage.get(runtime);
      coverageData.addCoverageData(data);
    }
    else {
      CoverageDataContainer coverageData = new CoverageDataContainer();
      coverageData.addCoverageData(data);
      this.timeToCoverage.put(runtime, coverageData);
    }
  }

  /**
   * Returns true if this class has stored any data.
   * @return true if data exists, false if not.
   */
  public boolean hasData() {
    if (this.timeToCoverage.keySet().isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * Returns the last runtime with sensor data. Null is returned if no data
   * exists.
   * @return the last runtime value.
   */
  public XMLGregorianCalendar getLastRuntime() {
    if (this.hasData()) {
      return this.timeToCoverage.lastKey();
    }
    return null;
  }

  /**
   * Returns the last batch of coverage data stored in this class. Null is
   * returned if no data exists.
   * @return the last batch of data.
   */
  public CoverageDataContainer getLatestBatch() {
    if (hasData()) {
      return this.timeToCoverage.get(this.timeToCoverage.lastKey());
    }
    return null;
  }
}
