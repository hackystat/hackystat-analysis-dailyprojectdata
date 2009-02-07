package org.hackystat.dailyprojectdata.resource.coverage.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestJAXBExtensions {
  /**
   * Test the property manipulation extensions to SensorData. 
   */
  @Test 
  public void testCoverageExtensions() {
    CoverageDailyProjectData dpd = new CoverageDailyProjectData();
    ConstructData data = new ConstructData();
    data.setNumCovered(1);
    data.setNumUncovered(1);
    dpd.getConstructData().add(data);
    assertTrue("Testing hasCoverageData", dpd.hasCoverageData());
    assertEquals("Testing percentage", 50, dpd.getPercentageCoverage());
  }
}
