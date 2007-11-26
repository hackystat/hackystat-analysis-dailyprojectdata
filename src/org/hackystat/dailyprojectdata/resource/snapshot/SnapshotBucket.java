package org.hackystat.dailyprojectdata.resource.snapshot;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Represents a "bucket" of time that the snapshot uses to query for data.
 * 
 * @author jsakuda
 */
class SnapshotBucket {
  /** Start time of the bucket. */
  private XMLGregorianCalendar startTime;

  /** End time of the bucket. */
  private XMLGregorianCalendar endTime;

  /**
   * Creates a new bucket of time.
   * 
   * @param startTime The start of the bucket.
   * @param endTime The end of the bucket.
   */
  SnapshotBucket(XMLGregorianCalendar startTime, XMLGregorianCalendar endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  /**
   * Gets the start time.
   * 
   * @return Returns the start time.
   */
  XMLGregorianCalendar getStartTime() {
    return this.startTime;
  }

  /**
   * Gets the end time.
   * 
   * @return Returns the end time.
   */
  XMLGregorianCalendar getEndTime() {
    return this.endTime;
  }
}
