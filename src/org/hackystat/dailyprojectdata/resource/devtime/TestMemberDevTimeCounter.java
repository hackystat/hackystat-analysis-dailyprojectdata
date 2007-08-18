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
public class TestMemberDevTimeCounter  {
  
  /**
   * Test the DevTimeCounter abstraction.
   * @throws Exception If problems occur.
   */
  @Test public void testMemberDevTimeCounter() throws Exception {
    XMLGregorianCalendar tstamp1 = Tstamp.makeTimestamp("2007-08-01T00:00:00");
    String member1 = "member1";
    String member2 = "member2";
    
    MemberDevTimeCounter counter = new MemberDevTimeCounter();
    assertEquals("Test empty", BigInteger.valueOf(0), counter.getTotalDevTime());
    counter.addMemberDevEvent(member1, tstamp1);
    assertEquals("Test first", BigInteger.valueOf(5), counter.getTotalDevTime());
    counter.addMemberDevEvent(member2, tstamp1);
    assertEquals("Test second", BigInteger.valueOf(10), counter.getTotalDevTime());

    assertEquals("Test third", BigInteger.valueOf(5), counter.getMemberDevTime(member1));
    assertEquals("Test fourth", BigInteger.valueOf(5), counter.getMemberDevTime(member2));
    assertEquals("Test fifth", 2, counter.getMembers().size());
  }
}
