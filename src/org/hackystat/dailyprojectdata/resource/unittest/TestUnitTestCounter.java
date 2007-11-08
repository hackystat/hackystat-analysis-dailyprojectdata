package org.hackystat.dailyprojectdata.resource.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Set;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.junit.Before;
import org.junit.Test;

/**
 * Test UnitTestDPDCounter class.
 *
 * @author Pavel Senin.
 *
 */
public class TestUnitTestCounter {

  private static final String testClassName = "org.hackystat.util.TestProxyProperty";
  private static final String testResource = "file://foo/bar/baz.txt";
  private static final String testCaseName = "testNormalFunctionality";
  private static final String testFailResult = "fail";
  private static final String testPassResult = "pass";
  private static final String testFailureString =  "Value of failure string";
  private static final String testErrorString = "Value of error string";
  private static final String user1 = "javadude@javatest.com";
  private static final String user2 = "javadude@javafoo.com";
  private static final BigInteger bigOne = BigInteger.valueOf(1);
  private static final BigInteger bigZero = BigInteger.valueOf(0);
  private UnitTestTestHelper testHelper;
  private UnitTestCounter counter;

  /**
   * Sets up testing environment.
   *
   * @throws Exception If problem occurs.
   */
  @Before
  public void setUp() throws Exception {
    this.testHelper = new UnitTestTestHelper();
    this.counter = new UnitTestCounter();
  }

  /**
   * Tests accounting.
   *
   * @throws Exception if problem occurs.
   */
  @Test
  public void addTest() throws Exception {

    // Create a passing sensor data item
    SensorData passData = this.testHelper.makeUnitTestEvent("2007-04-30T02:00:00", user1,
        testResource, testClassName, testPassResult, "50", testCaseName, testFailureString,
        testErrorString);

    // testing the counter with a passing data instance.
    this.counter.add(passData);
    Set<String> members = this.counter.getMembers();
    assertTrue("Should return test owner", members.contains(user1));

    // testing accounting.
    assertEquals("Check failure count", bigZero, this.counter.getFailCount(user1));
    assertEquals("Check succcess count", bigOne, this.counter.getPassCount(user1));
    
    // Create a failing sensor data item
    SensorData failData = this.testHelper.makeUnitTestEvent("2007-04-30T02:00:00", user1,
        testResource, testClassName, testFailResult, "50", testCaseName, testFailureString,
        testErrorString);

    // add the failure data instance.
    this.counter.add(failData);

    // test updated counter.
    assertEquals("Check failure count", bigOne, this.counter.getFailCount(user1));
    assertEquals("Check succcess count", bigOne, this.counter.getPassCount(user1));

    // test what happens when passed a missing user. 
    assertEquals("Checking missing user 1", bigZero, this.counter.getFailCount(user2));
    assertEquals("Check missing user 2", bigZero, this.counter.getPassCount(user2));
  }

}
