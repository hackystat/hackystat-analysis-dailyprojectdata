package org.hackystat.dailyprojectdata.resource.build;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Counts the number of successful and failed builds each user had.
 * 
 * @author jsakuda
 */
public class MemberBuildCounter {
  /** Mapping of users to the number of successful builds. */
  private Map<String, Integer> userToSuccessMap = new HashMap<String, Integer>();

  /** Mapping of users to the number of failed builds. */
  private Map<String, Integer> userToFailureMap = new HashMap<String, Integer>();
  
  /** All members with data in the counter. */
  private Set<String> members = new HashSet<String>();

  /**
   * Adds a successful build for a user.
   * 
   * @param user The user that had a successful build.
   */
  public void addSuccessfulBuild(String user) {
    if (this.userToSuccessMap.containsKey(user)) {
      Integer count = this.userToSuccessMap.get(user) + 1;
      this.userToSuccessMap.put(user, count);
    }
    else {
      this.userToSuccessMap.put(user, 1);
    }
    this.members.add(user);
  }

  /**
   * Adds a failed build for a user.
   * 
   * @param user The user that had a failed build.
   */
  public void addFailedBuild(String user) {
    if (this.userToFailureMap.containsKey(user)) {
      Integer count = this.userToFailureMap.get(user) + 1;
      this.userToFailureMap.put(user, count);
    }
    else {
      this.userToFailureMap.put(user, 1);
    }
    this.members.add(user);
  }

  /**
   * Gets a mapping of users to the number of successful builds they had.
   * 
   * @return Returns a mapping of users to the number of successful builds they had.
   */
  public Map<String, Integer> getSuccessfulBuilds() {
    return this.userToSuccessMap;
  }

  /**
   * Gets a mapping of users to the number of failed builds they had.
   * 
   * @return Returns a mapping of users to the number of failed builds they had.
   */
  public Map<String, Integer> getFailedBuilds() {
    return this.userToFailureMap;
  }
  
  /**
   * Gets a set of all the members.
   * 
   * @return Returns all the members.
   */
  public Set<String> getMembers() {
    return this.members;
  }
}
