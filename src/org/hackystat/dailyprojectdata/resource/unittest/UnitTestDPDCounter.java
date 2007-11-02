package org.hackystat.dailyprojectdata.resource.unittest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * Makes easier UnitTest DPD calculation.
 *
 * @author Pavel Senin.
 *
 */
public class UnitTestDPDCounter {

  /** The map of member emails to their DevTimeCounter. */
  private Map<String, UnitTestSimpleCounter> member2unitTestDPD;

  /**
   * Standard constructor does pretty much nothing.
   */
  public UnitTestDPDCounter() {
    member2unitTestDPD = new HashMap<String, UnitTestSimpleCounter>();
  }

  /**
   * Does accounting using sensor data provided.
   *
   * @param data unit test sensor data.
   */
  public void add(SensorData data) {

    // fixing the test owner (member)
    String owner = data.getOwner();

    Integer error = 0;
    Integer failure = 0;
    Integer passed = 0;
    Integer tests = 0;

    Properties props = data.getProperties();
    Boolean err = false;
    for (Property p : props.getProperty()) {
      if ((p.getKey().equalsIgnoreCase("errorString")) && (p.getValue() != null)) {
        error += 1;
        err = true;
      }
      else if ((p.getKey().equalsIgnoreCase("failureString")) && (p.getValue() != null)) {
        failure += 1;
        err = true;
      }
    }
    if (!err) {
      passed += 1;
    }
    tests += 1;

    // populating member data
    if (this.member2unitTestDPD.containsKey(owner)) {
      this.member2unitTestDPD.get(owner).update(error, failure, passed, tests);
    }
    else {
      this.member2unitTestDPD.put(owner, new UnitTestSimpleCounter(error, failure, passed, tests));
    }

  }

  /**
   * Returns the UnitTest failure count associated with Member, or zero if member does not exist.
   *
   * @param member The member.
   * @return The member's failure count.
   */
  public BigInteger getMemberFailureCount(String member) {
    if (member2unitTestDPD.containsKey(member)) {
      return member2unitTestDPD.get(member).getFailureCount();
    }
    else {
      return BigInteger.valueOf(0);
    }
  }

  /**
   * Returns a newly created Set containing all of the members in this UnitTestDPDCounter.
   *
   * @return The set of all members in this UnitTestDPDCounter.
   */
  public Set<String> getMembers() {
    Set<String> members = new HashSet<String>();
    members.addAll(member2unitTestDPD.keySet());
    return members;
  }

  /**
   * Returns the UnitTest success count associated with Member, or zero if member does not exist.
   *
   * @param member The member.
   * @return The member's success count.
   */
  public BigInteger getMemberSuccessCount(String member) {
    if (member2unitTestDPD.containsKey(member)) {
      return member2unitTestDPD.get(member).getSuccessCount();
    }
    else {
      return BigInteger.valueOf(0);
    }
  }

}
