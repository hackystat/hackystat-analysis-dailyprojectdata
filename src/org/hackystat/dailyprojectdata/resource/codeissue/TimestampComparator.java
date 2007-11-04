package org.hackystat.dailyprojectdata.resource.codeissue;

import java.io.Serializable;
import java.util.Comparator;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Compares <code>XmlGregorianCalendar</code> timestamps.
 *  
 * @author jsakuda
 */
public class TimestampComparator implements Comparator<XMLGregorianCalendar>, Serializable {
  /** Generated serial UID. */
  private static final long serialVersionUID = -8167649016257931201L;

  /** 
   * Compares two timestamps.
   * 
   * @param cal1 The first timestamp.
   * @param cal2 The second timestamp.
   * @return Returns -1 if cal1 < cal2, 1 if cal1 > cal2, otherwise returns 0.
   */
  @Override
  public int compare(XMLGregorianCalendar cal1, XMLGregorianCalendar cal2) {
    if (Tstamp.lessThan(cal1, cal2)) {
      return -1;
    }
    else if (Tstamp.greaterThan(cal1, cal2)) {
      return 1;
    }
    return 0;
  }
}
