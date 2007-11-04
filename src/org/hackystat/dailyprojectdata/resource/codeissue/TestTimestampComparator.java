package org.hackystat.dailyprojectdata.resource.codeissue;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.TestCase;

import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Test that <code>TimestampComparator</code> correctly compares timestamps.
 * 
 * @author jsakuda
 * 
 */
public class TestTimestampComparator extends TestCase {
  
  /** Tests that timestamps are correctly compared. */
  @Test
  public void testCompare() {
    XMLGregorianCalendar timestamp = Tstamp.makeTimestamp(Day.getInstance(2007, 11, 2));
    XMLGregorianCalendar incrementedTimestamp = Tstamp.incrementDays(timestamp, 1);

    TimestampComparator comparator = new TimestampComparator();
    assertSame("Tstamp should be less than incremented tstamp.", -1, comparator.compare(
        timestamp, incrementedTimestamp));

    assertSame("Incremented tstamp should be greater than tstamp.", 1, comparator.compare(
        incrementedTimestamp, timestamp));

    assertSame("Tstamp should be equal to itself.", 0, comparator
        .compare(timestamp, timestamp));
  }
}
