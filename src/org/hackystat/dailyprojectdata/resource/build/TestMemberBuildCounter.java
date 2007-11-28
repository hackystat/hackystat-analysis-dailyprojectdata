package org.hackystat.dailyprojectdata.resource.build;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests that the <code>MemberBuildCounter</code> counts correctly.
 * 
 * @author jsakuda
 *
 */
public class TestMemberBuildCounter {
  private String member1 = "member1";
  private String member2 = "member2";
  
  /** Tests the successful and failed builds are counted correctly. */
  @Test public void testMemberCounts() {
    MemberBuildCounter counter = new MemberBuildCounter();
    
    // add 5 failures for member1
    for (int i = 0; i < 5; i++) {
      counter.addFailedBuild(this.member1);
    }
    
    // add 3 successful builds for member1
    for (int i = 0; i < 3; i++) {
      counter.addSuccessfulBuild(this.member1);
    }
    
    // add 7 successful builds for member2
    for (int i = 0; i < 7; i++) {
      counter.addSuccessfulBuild(this.member2);
    }
    
    Set<String> members = counter.getMembers();
    assertSame("Should have 2 members", 2, members.size());
    
    Map<String, Integer> failedBuilds = counter.getFailedBuilds();
    Map<String, Integer> successfulBuilds = counter.getSuccessfulBuilds();
    
    assertSame("member1 has 5 failures.", 5, failedBuilds.get(this.member1));
    assertNull("member2 should have no failure mapping.", failedBuilds.get(this.member2));
    assertSame("member1 has 3 successes.", 3, successfulBuilds.get(this.member1));
    assertSame("member2 has 7 successes.", 7, successfulBuilds.get(this.member2));
  }
}
