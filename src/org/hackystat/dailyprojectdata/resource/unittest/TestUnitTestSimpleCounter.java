package org.hackystat.dailyprojectdata.resource.unittest;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests simple counter.
 *
 * @author senin
 *
 */
public class TestUnitTestSimpleCounter {

  // counter under the test
  private UnitTestSimpleCounter counter;

  private static final String test1Title = "Testing default constructor";
  private static final String test2Title = "Testing paramterized constructor";
  private static final String test3Title = "Testing update method";

  /**
   * Sets up the testing environment.
   *
   * @throws Exception if error occurs.
   */
  @Before
  public void setUp() throws Exception {
    this.counter = new UnitTestSimpleCounter();
  }

  /**
   * Tests very default constructor.
   */
  @Test
  public void testUnitTestSimpleCounter() {
    assertEquals(test1Title, BigInteger.valueOf(0), this.counter.getFailureCount());
    assertEquals(test1Title, BigInteger.valueOf(0), this.counter.getSuccessCount());
    assertEquals(test1Title, BigInteger.valueOf(0), this.counter.getTestCount());
    assertEquals(test1Title, BigInteger.valueOf(0), this.counter.getErrorsCount());
  }

  /**
   * Tests constructor with predefined fields along with update method.
   */
  @Test
  public void testUpdate() {
    this.counter = new UnitTestSimpleCounter(1, 2, 3, 4);
    assertEquals(test2Title, BigInteger.valueOf(2), this.counter.getFailureCount());
    assertEquals(test2Title, BigInteger.valueOf(3), this.counter.getSuccessCount());
    assertEquals(test2Title, BigInteger.valueOf(4), this.counter.getTestCount());
    assertEquals(test2Title, BigInteger.valueOf(1), this.counter.getErrorsCount());

    this.counter.update(5, 6, 7, 8);
    assertEquals(test3Title, BigInteger.valueOf(8), this.counter.getFailureCount());
    assertEquals(test3Title, BigInteger.valueOf(10), this.counter.getSuccessCount());
    assertEquals(test3Title, BigInteger.valueOf(12), this.counter.getTestCount());
    assertEquals(test3Title, BigInteger.valueOf(6), this.counter.getErrorsCount());
  }

}
