package org.hackystat.dailyprojectdata.resource.codeissue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests that the <code>MemberCodeIssueCounter</code> counts correctly.
 * 
 * @author jsakuda
 */
public class TestMemberCodeIssueCounter {
  private String member1 = "Member1";
  private String member2 = "Member2";
  private String member3 = "Member3";

  private String tool1 = "Tool1";
  private String tool2 = "Tool2";

  private String type1 = "Type_Type1";
  private String type2 = "Type_Type2";

  /** Tests the the counter correctly counts issues. */
  @Test
  public void testCounter() {
    MemberCodeIssueCounter counter = new MemberCodeIssueCounter();
    counter.addMemberCodeIssue(this.member1, this.tool1, this.type1, 2);
    counter.addMemberCodeIssue(this.member1, this.tool1, this.type1, 4);
    counter.addMemberCodeIssue(this.member2, this.tool1, this.type1, 1);
    counter.addMemberCodeIssue(this.member3, this.tool1, this.type1, 1);

    counter.addMemberCodeIssue(this.member1, this.tool2, this.type2, 2);
    counter.addMemberCodeIssue(this.member2, this.tool2, this.type2, 3);

    counter.addMemberCodeIssue(this.member3, this.tool2, null, 0);

    Set<String> members = counter.getMembers();
    assertEquals("Should have 3 memebers in counter.", 3, members.size());
    
    ToolToTypeCounter member1CodeIssueCounts = counter.getMemberCodeIssueCounts(this.member1);
    Map<String, Integer> typeCounts = member1CodeIssueCounts.getTypeCounts(this.tool1);

    assertSame("Tool1/Type1 should have count 6 for Memeber1.", 6, typeCounts.get(this.type1));
    
    typeCounts = member1CodeIssueCounts.getTypeCounts(this.tool2);
    assertSame("Tool2/Type2 should have count 2 for Memeber1.", 2, typeCounts.get(this.type2));

    ToolToTypeCounter member3CodeIssueCounts = counter.getMemberCodeIssueCounts(this.member3);
    typeCounts = member3CodeIssueCounts.getTypeCounts(this.tool2);
    assertTrue("Member3 should have no types for tool2", typeCounts.isEmpty());
  }
}
