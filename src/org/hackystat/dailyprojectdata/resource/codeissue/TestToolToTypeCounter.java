package org.hackystat.dailyprojectdata.resource.codeissue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Map;

import org.junit.Test;

/**
 * Tests that the <code>ToolToTypeCounter</code> counts correctly.
 * 
 * @author jsakuda
 */
public class TestToolToTypeCounter {
  private String tool1 = "Tool1";
  private String tool2 = "Tool2";

  private String type1 = "Type:Type1";
  private String type2 = "Type:Type2";
  private String type3 = "Type:Type3";
  
  /** Tests that the counter correctly counts issues. */
  @Test public void testCounter() {
    ToolToTypeCounter counter = new ToolToTypeCounter();
    counter.add(tool1, type1, 5);
    counter.add(tool1, type2, 2);
    counter.add(tool1, type1, 2);
    counter.add(tool2, type3, 3);
    
    assertEquals("Should have 2 tools.", 2, counter.getTools().size());
    
    Map<String, Integer> typeCounts = counter.getTypeCounts(tool1);
    assertSame("Should have 7 issues of type1", 7, typeCounts.get(type1));
    assertSame("Should have 2 issues of type2", 2, typeCounts.get(type2));
  }
}
