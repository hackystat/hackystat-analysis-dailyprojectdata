package org.hackystat.dailyprojectdata.resource.issue;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.dailyprojectdata.resource.issue.jaxb.IssueData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Parser for the issue sensordata to IssueData.
 * @author Shaoxuan Zhang
 *
 */
public class IssueDataParser {

  //static String copied from org.hackystat.sensor.ant.issue.IssueEntry
  /** property key of ID. */
  protected static final String ID_PROPERTY_KEY = "Id";
  /** property key of TYPE. */
  protected static final String TYPE_PROPERTY_KEY = "Type";
  /** property key of STATUS. */
  protected static final String STATUS_PROPERTY_KEY = "Status";
  /** property key of PRIORITY. */
  protected static final String PRIORITY_PROPERTY_KEY = "Priority";
  /** property key of MILESTONE. */
  protected static final String MILESTONE_PROPERTY_KEY = "Milestone";
  /** property key of OWNER. */
  protected static final String OWNER_PROPERTY_KEY = "Owner";

  /** timestamp separator in property value. */
  protected static final String TIMESTAMP_SEPARATOR = "--";
  
  private Logger logger;
  private List<String> openIssueStatus = Arrays.asList(new String[]{"New", "Accepted", "Started"});
  
  /**
   * @param logger the logger.
   */
  public IssueDataParser(final Logger logger) {
    this.logger = logger;
  }
  
  /**
   * Determine if the status value means open.
   * @param statusValue the status value.
   * @return true if it is open.
   */
  public boolean isOpenStatus(String statusValue) {
    return statusValue != null && openIssueStatus.contains(statusValue);
  }
  
  /**
   * Get the state of the issue on the given time.
   * @param issueSensorData the given sensordata.
   * @param timestamp the time.
   * @return the IssueData.
   */
  public IssueData getIssueDpd(SensorData issueSensorData, final XMLGregorianCalendar timestamp) {
    IssueData issueDpd = new IssueData();

    //get id
    for (Property property : issueSensorData.getProperties().getProperty()) {
      if (ID_PROPERTY_KEY.equals(property.getKey())) {
        issueDpd.setId(Integer.valueOf(property.getValue()));
        break;
      }
    }
    
    //get latest type
    issueDpd.setType(this.getValueWithKeyWhen(issueSensorData, TYPE_PROPERTY_KEY, timestamp));
    
    //get latest status
    issueDpd.setStatus(this.getValueWithKeyWhen(issueSensorData, STATUS_PROPERTY_KEY, timestamp));
    
    //get latest priority
    issueDpd.setPriority(
        this.getValueWithKeyWhen(issueSensorData, PRIORITY_PROPERTY_KEY, timestamp));
    
    //get latest milestone
    issueDpd.setMilestone(
        this.getValueWithKeyWhen(issueSensorData, MILESTONE_PROPERTY_KEY, timestamp));
    
    //get latest owner
    issueDpd.setOwner(this.getValueWithKeyWhen(issueSensorData, OWNER_PROPERTY_KEY, timestamp));
    
    return issueDpd;
  }

  /**
   * Return the value with the given key in the given time within the given sensordata.
   * @param issueSensorData the sensordata.
   * @param key the property key
   * @param timestamp the time.
   * @return the latest value, null if not found.
   */
  public String getValueWithKeyWhen(SensorData issueSensorData, String key, 
      XMLGregorianCalendar timestamp) {
    XMLGregorianCalendar latestTime = null;
    String value = null;
    for (Property property : issueSensorData.getProperties().getProperty()) {
      if (key.equals(property.getKey())) {
        XMLGregorianCalendar newTimestamp = null;
        try {
          newTimestamp = extractTimestamp(property.getValue());
        }
        catch (Exception e) {
          logger.warning("Error when extracting timestamp from [" + property.getValue() + "]");
        }
        if (newTimestamp != null && !Tstamp.greaterThan(newTimestamp, timestamp) && 
            (latestTime == null || Tstamp.lessThan(latestTime, newTimestamp))) {
          latestTime = newTimestamp;
          value = extractValue(property.getValue());
        }
      }
    }
    return value;
  }

  /**
   * Extract timestamp from formatted string.
   * @param value the string.
   * @return the timestamp.
   * @throws Exception if the string is not formatted.
   */
  private static XMLGregorianCalendar extractTimestamp(String value) throws Exception {
    int startIndex = value.indexOf(TIMESTAMP_SEPARATOR) + TIMESTAMP_SEPARATOR.length();
    return Tstamp.makeTimestamp(value.substring(startIndex));
  }

  /**
   * Extract value from formatted string.
   * @param string the string.
   * @return the value.
   */
  private static String extractValue(String string) {
    return string.substring(0, string.indexOf(TIMESTAMP_SEPARATOR));
  }
}
