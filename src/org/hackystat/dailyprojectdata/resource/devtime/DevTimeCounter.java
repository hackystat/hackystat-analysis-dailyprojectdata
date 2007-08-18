package org.hackystat.dailyprojectdata.resource.devtime;

import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Provides a facility for counting up the DevTime associated with a series of timestamps
 * associated with DevEvent sensor data.  
 * The DevTimeCounter partitions a day into 288 five minute intervals.  If at least one 
 * DevEvent timestamp occurs within an interval, that entire five minute interval counts
 * as DevTime.  At any time, the getDevTime() method can be called to find the total 
 * amount of DevTime accumulated so far.   DevTime for a day can range from 0 minutes 
 * to 1440 minutes, in five minute increments. 
 * 
 * Note that the DevTimeCounter looks only at the hours, minutes, and seconds associated with 
 * a timestamp.  The client is responsible for assuring that all of the timestamps are associated
 * with the same day. 
 * 
 * @author Philip Johnson
 */
public class DevTimeCounter {
  
  /** Array of five minute intervals, initialized to false, indicating zero DevTime. */
  private boolean[] devTimeArray = new boolean[288];
  
  /** Create a new DevTimeCounter, initialized to zero DevTime. */
  public DevTimeCounter () {
    // Do nothing
  }
  
  /**
   * Update the DevTimeCounter with a new DevEvent.  Its five minute interval will now count
   * as DevTime (if it didn't already). 
   * @param timestamp The timestamp to add. 
   */
  public void addDevEvent(XMLGregorianCalendar timestamp) {
    int hours = timestamp.getHour();
    int minutes = timestamp.getMinute();
    int seconds = timestamp.getSecond();
    int totalSeconds = seconds + (minutes * 60) + (hours * 60 * 60);
    int binSize = (60 * 60 * 24) / 288;
    int index = (totalSeconds / binSize);
    devTimeArray[index] = true;
  }
  
  /**
   * Returns the total DevTime (in minutes, as a multiple of five) associated with this
   * DevTimeCounter.
   * Returns a BigInteger because that's what the XML wants.  
   * @return The total DevTime, from 0 to 1440 (5 * 288). 
   */
  public BigInteger getDevTime() {
    int devTime = 0;
    for (int index = 0; index < 288; index++) {
      if (devTimeArray[index]) {
        devTime += 5;
      }
    }
    return BigInteger.valueOf(devTime);
  }
  

}
