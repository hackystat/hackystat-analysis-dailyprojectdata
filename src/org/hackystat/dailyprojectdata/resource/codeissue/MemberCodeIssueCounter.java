package org.hackystat.dailyprojectdata.resource.codeissue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Counts code issue instances for all members.
 * 
 * @author jsakuda
 */
public class MemberCodeIssueCounter {
  /** Mapping of tool/type to number of occurrences. */  
  private Map<String, ToolToTypeCounter> memberCodeIssueMap;
  
  /** Creates and initializes a new counter. */
  public MemberCodeIssueCounter() {
    this.memberCodeIssueMap = new HashMap<String, ToolToTypeCounter>();
  }
  
  /**
   * Add a code issue instance for a member.
   * 
   * @param member The member the code issue is for.
   * @param tool The tool used by the member to generate the code issue.
   * @param type The type of the error produced by the given tool.
   * @param count The number of issues of the tool/type pair given.
   */
  public void addMemberCodeIssue(String member, String tool, String type, int count) {
    if (!this.memberCodeIssueMap.containsKey(member)) {
      this.memberCodeIssueMap.put(member, new ToolToTypeCounter());
    }
    
    ToolToTypeCounter toolToTypeCounter = this.memberCodeIssueMap.get(member);
    toolToTypeCounter.add(tool, type, count);
  }
  
  /**
   * Gets the set of all members that have issues in the counter.
   * 
   * @return Returns the set of members that have code issues in the counter.
   */
  public Set<String> getMembers() {
    return this.memberCodeIssueMap.keySet();
  }
  
  /**
   * Gets the <code>ToolToTypeCounter</code> for the given member.
   * 
   * @param member The member to get the counts for.
   * @return Returns the <code>ToolToTypeCounter</code> for the given member.
   */
  public ToolToTypeCounter getMemberCodeIssueCounts(String member) {
    return this.memberCodeIssueMap.get(member);
  }
}
