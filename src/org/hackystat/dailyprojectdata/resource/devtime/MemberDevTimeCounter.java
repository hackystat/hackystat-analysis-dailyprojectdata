package org.hackystat.dailyprojectdata.resource.devtime;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * An abstraction that collects the DevTime associated with a set of Project Members.
 * Implemented using a map of Members to DevTimeCounter instances.  
 * @author Philip Johnson
 *
 */
public class MemberDevTimeCounter {
  
  /** The map of member emails to their DevTimeCounter. */ 
  private Map<String, DevTimeCounter> member2devtime = new HashMap<String, DevTimeCounter>();
  
  /** Create a new MemberDevTimeCounter. */
  public MemberDevTimeCounter() {
    // does nothing. 
  }

  /**
   * Updates this abstraction with the DevEvent associated with the Project member. 
   * @param member The member. 
   * @param timestamp The timestamp associated with the DevEvent associated with the member. 
   */
  public void addMemberDevEvent(String member, XMLGregorianCalendar timestamp) {
    if (!member2devtime.containsKey(member)) {
      member2devtime.put(member, new DevTimeCounter());
    }
    member2devtime.get(member).addDevEvent(timestamp);
  }
  
  /**
   * Return the aggregate DevTime for all members. 
   * @return The total DevTime. 
   */
  public BigInteger getTotalDevTime() {
    BigInteger totalDevTime = BigInteger.valueOf(0);
    for (DevTimeCounter counter : member2devtime.values()) {
      totalDevTime = totalDevTime.add(counter.getDevTime());
    }
    return totalDevTime;
  }
  
  /**
   * Returns the DevTime associated with Member, or zero if member does not exist. 
   * @param member The member
   * @return The member's devtime. 
   */
  public BigInteger getMemberDevTime(String member) {
    if (member2devtime.containsKey(member)) {
      return member2devtime.get(member).getDevTime();
    }
    else {
      return BigInteger.valueOf(0);
    }
  }
  
  /**
   * Returns a newly created Set containing all of the members in this MemberDevTimeCounter. 
   * @return The set of all members in this MemberDevTimeCounter. 
   */
  public Set<String> getMembers() {
    Set<String> members = new HashSet<String>();
    members.addAll(member2devtime.keySet());
    return members;
  }
}
