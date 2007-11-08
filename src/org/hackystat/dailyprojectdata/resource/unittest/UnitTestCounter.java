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
 * A data structure that collects the Unit Test pass and fail counts for each member.
 *
 * @author Pavel Senin, Philip Johnson
 *
 */
public class UnitTestCounter {

  /** Maps members to the number of successful unit test invocations. */
  private Map<String, Integer> passCount = new HashMap<String, Integer>();
  
  /** Maps members to the number of unsuccessful unit test invocations. */
  private Map<String, Integer> failCount = new HashMap<String, Integer>();

  /**
   * Standard constructor does pretty much nothing.
   */
  public UnitTestCounter() {
    // No need to do anything.
  }

  /**
   * Does accounting using sensor data provided.
   *
   * @param data unit test sensor data.
   */
  public void add(SensorData data) {

    // Initialize the maps for this user if necessary.
    String owner = data.getOwner();
    if (!passCount.containsKey(owner)) {
      passCount.put(owner, 0);
      failCount.put(owner, 0);
    }
    
    // Now update the pass or fail count. 
    // Result property must exist and must be pass or fail.
    String result = getValue("Result", data);
    if ((result != null) && (result.equalsIgnoreCase("pass"))) {
      passCount.put(owner, (passCount.get(owner) + 1));
    }
    if ((result != null) && (result.equalsIgnoreCase("fail"))) {
      failCount.put(owner, (failCount.get(owner) + 1));
    }
  }
  
  /**
   * Returns the (first) value associated with key in Properties, or null if not found.
   * Assumes that keys are unique.
   * @param key The key
   * @param data The Sensor Data instance.
   * @return The value associated with key, or null if not found. 
   */
  private String getValue(String key, SensorData data) {
    Properties properties = data.getProperties();
    for (Property property : properties.getProperty()) {
      if (property.getKey().equals(key)) {
        return property.getValue();
      }
    }
    return null;
  }

  /**
   * Returns the UnitTest failure count associated with Member, or zero if member does not exist.
   *
   * @param member The member.
   * @return The member's failure count.
   */
  public BigInteger getFailCount(String member) {
    int numFails =  ((failCount.containsKey(member)) ? failCount.get(member) : 0);
    return BigInteger.valueOf(numFails);
  }
  
  /**
   * Returns the UnitTest pass count associated with Member, or zero if member does not exist.
   *
   * @param member The member.
   * @return The member's pass count.
   */
  public BigInteger getPassCount(String member) {
    int numSuccess =  ((passCount.containsKey(member)) ? passCount.get(member) : 0);
    return BigInteger.valueOf(numSuccess);
  }

  /**
   * Returns a newly created Set containing all of the members in this Counter.
   *
   * @return The set of all members in this UnitTestDPDCounter.
   */
  public Set<String> getMembers() {
    Set<String> members = new HashSet<String>();
    members.addAll(failCount.keySet());
    return members;
  }

}
