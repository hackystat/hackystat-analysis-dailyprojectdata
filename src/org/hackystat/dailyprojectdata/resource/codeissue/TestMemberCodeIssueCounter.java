package org.hackystat.dailyprojectdata.resource.codeissue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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

  private String category1 = "Category1";
  private String category2 = "Category2";

  /** Tool/category pair for test. */
  private ToolCategoryPair pair1 = new ToolCategoryPair(this.tool1, this.category1);
  /** Tool/category pair for test. */
  private ToolCategoryPair pair2 = new ToolCategoryPair(this.tool2, this.category2);
  /** Tool/category pair for zero data test. */
  private ToolCategoryPair pair3 = new ToolCategoryPair(this.tool2, null);

  /** Tests the the counter correctly counts issues. */
  @Test
  public void testCounter() {
    MemberCodeIssueCounter counter = new MemberCodeIssueCounter();
    counter.addMemberCodeIssue(this.member1, this.tool1, this.category1);
    counter.addMemberCodeIssue(this.member1, this.tool1, this.category1);
    counter.addMemberCodeIssue(this.member2, this.tool1, this.category1);
    counter.addMemberCodeIssue(this.member3, this.tool1, this.category1);

    counter.addMemberCodeIssue(this.member1, this.tool2, this.category2);
    counter.addMemberCodeIssue(this.member2, this.tool2, this.category2);

    counter.addMemberCodeIssue(this.member3, this.tool2, null);

    Set<String> members = counter.getMembers();
    assertEquals("Should have 3 memebers in counter.", 3, members.size());

    Map<ToolCategoryPair, Integer> member1IssueCounts = counter
        .getMemeberCodeIssueCounts(this.member1);

    assertSame("Tool1/Category1 should have count 2 for Memeber1.", 2, member1IssueCounts
        .get(this.pair1));
    assertSame("Tool2/Category2 should have count 1 for Memeber1.", 1, member1IssueCounts
        .get(this.pair2));

    Map<ToolCategoryPair, Integer> member3IssueCounts = counter
        .getMemeberCodeIssueCounts(this.member3);
    assertSame("Should have 0 as the count because no category was given.", 0, member3IssueCounts
        .get(this.pair3));
  }
}
