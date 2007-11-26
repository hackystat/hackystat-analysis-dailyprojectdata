package org.hackystat.dailyprojectdata.resource.snapshot;

import static org.junit.Assert.*;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the <code>SensorDataRef</code>s are properly compared.
 * 
 * @author jsakuda
 */
public class TestSensorDataRefComparator {
  private SensorDataRef ref1;
  
  private SensorDataRef ref2;
  
  /**
   * Creates dummy sensor data refs for testing.
   * 
   * @throws Exception Thrown if there is an error.
   */
  @Before public void setupDataRefs() throws Exception {
    XMLGregorianCalendar tstamp1 = Tstamp.makeTimestamp("2007-10-30T02:00:00");
    XMLGregorianCalendar tstamp2 = Tstamp.makeTimestamp("2007-10-30T04:00:00");
    
    this.ref1 = new SensorDataRef();
    this.ref1.setTimestamp(tstamp1);
    
    this.ref2 = new SensorDataRef();
    this.ref2.setTimestamp(tstamp2);
  }
  
  
  /** Tests the comparing of data refs in ascending order. */
  @Test
  public void testCompareAscending() {
    SensorDataRefComparator comparator = new SensorDataRefComparator(true);
    
    assertEquals("ref1 should be before ref2", -1, comparator.compare(this.ref1, this.ref2));
  }
  
  /** Tests the comparing of data refs in descending order. */
  @Test
  public void testCompareDescending() {
    SensorDataRefComparator comparator = new SensorDataRefComparator(false);
    
    assertEquals("ref1 should be after ref2", 1, comparator.compare(this.ref1, this.ref2));
  }

  /** Tests the comparing of data refs with the same timestamp. */
  @Test
  public void testCompareSame() {
    SensorDataRefComparator comparator = new SensorDataRefComparator(false);
    
    assertEquals("ref1 should be equal to itself", 0, comparator.compare(this.ref1, this.ref1));
  }

}
