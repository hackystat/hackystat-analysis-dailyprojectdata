package org.hackystat.dailyprojectdata.resource.filemetric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Provides a facility for counting the FileMetrics associated with a
 * SensorData. It only counts the FileMetrics from the 'last' run of the same
 * tool. This ensures that the FileMetric size is the latest snapshot of the
 * Project's size.
 * 
 * The FileMetricCounter currently only looks at TotalLines.
 * 
 * @author Cam Moore
 * 
 */
public class FileMetricCounter {

  private String lastTool;
  private XMLGregorianCalendar lastTime;
  private ArrayList<SensorData> fileMetrics;
  private SensorBaseClient sbClient;
  private long totalLines;

  /**
   * Constructs a FileMetricCounter.
   * 
   * @param client the SensorBaseClient to use to get the SensorData.
   */
  public FileMetricCounter(SensorBaseClient client) {
    this.lastTool = "";
    Day first = Day.getInstance(1000, 0, 1); // pick a very early Day
    this.lastTime = Tstamp.makeTimestamp(first);
    this.fileMetrics = new ArrayList<SensorData>();
    this.sbClient = client;
    this.totalLines = 0;
  }

  /**
   * Adds the SensorData to the list of FileMetrics if it is in the last run of the
   * size counting tool.  Updates the total lines value.  If this sensor data represents
   * a new run of a size counting tool then the total lines and list of file metrics is 
   * reset.
   * @param ref a SensorDataRef, the sensor data to add.
   */
  public void add(SensorDataRef ref) {
    try {
      SensorData data = sbClient.getSensorData(ref);
      XMLGregorianCalendar refTime = data.getRuntime();
      if (Tstamp.equal(lastTime, refTime)) {
        data = sbClient.getSensorData(ref);
        if (lastTool.equals(data.getTool())) {
          // only add data from the same tool.
          List<Property> props = data.getProperties().getProperty();
          for (Iterator<Property> iter = props.iterator(); iter.hasNext();) {
            Property prop = iter.next();
            if (prop.getKey().equals("TotalLines")) {
              totalLines += Long.parseLong(prop.getValue());
            }
          }
          fileMetrics.add(data);
        }

      } 
      else if (Tstamp.greaterThan(refTime, lastTime)) {
        // newer time so replace everything
        lastTime = refTime;
        fileMetrics = new ArrayList<SensorData>();
        totalLines = 0;
        lastTool = data.getTool();
        List<Property> props = data.getProperties().getProperty();
        for (Iterator<Property> iter = props.iterator(); iter.hasNext();) {
          Property prop = iter.next();
          if (prop.getKey().equals("TotalLines")) {
            totalLines += Long.parseLong(prop.getValue());
          }
        }
        fileMetrics.add(data);
      }
    } 
    catch (SensorBaseClientException e) {
      lastTool = "";
    }

  }

  /**
   * @return The total lines as a BigInteger.
   */
  public BigInteger getTotalLines() {
    return BigInteger.valueOf(totalLines);
  }

  /**
   * @return The individual FileMetrics SensorDataRefs.
   */
  public List<SensorData> getFileMetrics() {
    return fileMetrics;
  }

  /**
   * @return The last time a size counting tool was run.
   */
  public XMLGregorianCalendar getLastTime() {
    return lastTime;
  }

  /**
   * @param data a SensorData.
   * @return The total lines in the given SensorData or 0 if the property "TotalLines"
   * is undefined.
   */
  public BigInteger getTotalLines(SensorData data) {
    long totalLines = 0;
    List<Property> props = data.getProperties().getProperty();
    for (Iterator<Property> iter = props.iterator(); iter.hasNext();) {
      Property prop = iter.next();
      if (prop.getKey().equals("TotalLines")) {
        totalLines += Long.parseLong(prop.getValue());
      }
    }
    return BigInteger.valueOf(totalLines);
  }
}
