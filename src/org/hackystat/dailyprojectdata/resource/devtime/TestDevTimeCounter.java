package org.hackystat.dailyprojectdata.resource.devtime;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the DevTimeCounter.
 * @author Philip Johnson
 */
public class TestDevTimeCounter  {
  
  /**
   * Test the DevTimeCounter abstraction.
   * @throws Exception If problems occur.
   */
  @Test public void testDevTimeCounter() throws Exception {
    XMLGregorianCalendar tstamp1 = Tstamp.makeTimestamp("2007-08-01T00:00:00");
    XMLGregorianCalendar tstamp2 = Tstamp.makeTimestamp("2007-08-01T00:04:59");
    XMLGregorianCalendar tstamp3 = Tstamp.makeTimestamp("2007-08-01T00:05:00");
    XMLGregorianCalendar tstamp4 = Tstamp.makeTimestamp("2007-08-01T00:05:01");
    XMLGregorianCalendar tstamp5 = Tstamp.makeTimestamp("2007-08-01T00:10:00");
    XMLGregorianCalendar tstamp6 = Tstamp.makeTimestamp("2007-08-01T23:59:59");
    
    DevTimeCounter counter = new DevTimeCounter();
    assertEquals("Test empty", BigInteger.valueOf(0), counter.getDevTime());
    counter.addDevEvent(tstamp1);
    assertEquals("Test first", BigInteger.valueOf(5), counter.getDevTime());
    counter.addDevEvent(tstamp2);
    assertEquals("Test second", BigInteger.valueOf(5), counter.getDevTime());
    counter.addDevEvent(tstamp3);
    assertEquals("Test third", BigInteger.valueOf(10), counter.getDevTime());
    counter.addDevEvent(tstamp4);
    assertEquals("Test fourth", BigInteger.valueOf(10), counter.getDevTime());
    counter.addDevEvent(tstamp5);
    assertEquals("Test fifth", BigInteger.valueOf(15), counter.getDevTime());
    counter.addDevEvent(tstamp6);
    assertEquals("Test sixth", BigInteger.valueOf(20), counter.getDevTime());
  }
}
