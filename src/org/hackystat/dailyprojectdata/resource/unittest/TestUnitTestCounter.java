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

  // counter under the test.
  private UnitTestCounter counter;

  private static final String testClassName = "org.hackystat.core.installer.util.TestProxyProperty";

  private static final String testResource = "file://foo/bar/baz.txt";

  private static final String testCaseName = "testNormalFunctionality";

  private static final String testFailureResult = "failure";

  private static final String testSuccessResult = "success";

  private static final String testFailureString =
                                         "Value should be the same. expected:<[8]0> but was:<[9]0>";

  private static final String testErrorString = "Value of error string";

  private static final String user1 = "javadude@javatest.com";

  private static final String user2 = "javadude@javafoo.com";

  private UnitTestTestHelper testHelper;

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

    // populating testing data
    SensorData testData1 = this.testHelper.makeUnitTestEvent("2007-04-30T02:00:00", user1,
        testResource, testClassName, testFailureResult, "50", testCaseName, testFailureString,
        testErrorString);

    // testing member
    this.counter.add(testData1);
    Set<String> members = this.counter.getMembers();
    assertTrue("Should return test owner", members.contains(user1));

    // testing failure accounting
    assertEquals("Should return  failure count", BigInteger.valueOf(1), this.counter
        .getMemberFailureCount(user1));
    assertEquals("Should return succcess count", BigInteger.valueOf(0), this.counter
        .getMemberSuccessCount(user1));

    // testing default accounting behavior
    assertEquals("Should return failure count", BigInteger.valueOf(0), this.counter
        .getMemberFailureCount(user2));
    assertEquals("Should  return succcess count", BigInteger.valueOf(0), this.counter
        .getMemberSuccessCount(user2));

    // populating testing data for user 2 and updating data for user 1
    testData1 = this.testHelper.makeUnitTestEvent("2007-04-30T02:10:00", user1, testResource,
        testClassName, testSuccessResult, "50", testCaseName, null, null);
    SensorData testData2 = this.testHelper.makeUnitTestEvent("2007-04-30T02:00:00", user2,
        testResource, testClassName, testSuccessResult, "30", testCaseName, null, null);

    // testing member data
    this.counter.add(testData1);
    this.counter.add(testData2);
    members = this.counter.getMembers();
    assertTrue("Should return test owner", members.contains(user1));
    assertTrue("Should return test owner", members.contains(user2));

    // testing failure accounting
    assertEquals("Should return failure  count", BigInteger.valueOf(1), this.counter
        .getMemberFailureCount(user1));
    assertEquals("Should return succcess  count", BigInteger.valueOf(1), this.counter
        .getMemberSuccessCount(user1));
    assertEquals("Should return  failure count", BigInteger.valueOf(0), this.counter
        .getMemberFailureCount(user2));
    assertEquals("Should return  succcess count", BigInteger.valueOf(1), this.counter
        .getMemberSuccessCount(user2));

  }

}
