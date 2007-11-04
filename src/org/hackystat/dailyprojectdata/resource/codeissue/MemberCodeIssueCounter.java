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
  /** Mapping of tool/category to number of occurrences. */
  private Map<String, Map<ToolCategoryPair, Integer>> memberCodeIssueMap;
  
  /** Creates and initializes a new counter. */
  public MemberCodeIssueCounter() {
    this.memberCodeIssueMap = new HashMap<String, Map<ToolCategoryPair,Integer>>();
  }
  
  /**
   * Add a code issue instance for a member.
   * 
   * @param member The member the code issue is for.
   * @param tool The tool used by the member to generate the code issue.
   * @param category The category of the error produced by the given tool.
   */
  public void addMemberCodeIssue(String member, String tool, String category) {
    if (!this.memberCodeIssueMap.containsKey(member)) {
      this.memberCodeIssueMap.put(member, new HashMap<ToolCategoryPair, Integer>());
    }
    
    Map<ToolCategoryPair, Integer> toolCategoryMap = this.memberCodeIssueMap.get(member);
    ToolCategoryPair toolCategoryPair = new ToolCategoryPair(tool, category);
    if (toolCategoryMap.containsKey(toolCategoryPair)) {
      // tool/category combination exists, increment count
      Integer issueCount = toolCategoryMap.get(toolCategoryPair);
      toolCategoryMap.put(toolCategoryPair, issueCount + 1);
    }
    else {
      if (category == null) {
        // no entry exists, but only tool is defined so it represents zero data issue
        toolCategoryMap.put(toolCategoryPair, 0);
      }
      else {
        // no entry exists, but tool/category defined indicates an error
        toolCategoryMap.put(toolCategoryPair, 1);
      }
    }
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
   * Gets the tool/category to issue count mapping for a single member.
   * 
   * @param member The member to get the mapping for.
   * @return Returns the tool/category to issue count mapping for the given member.
   */
  public Map<ToolCategoryPair, Integer> getMemeberCodeIssueCounts(String member) {
    return this.memberCodeIssueMap.get(member);
  }
}
