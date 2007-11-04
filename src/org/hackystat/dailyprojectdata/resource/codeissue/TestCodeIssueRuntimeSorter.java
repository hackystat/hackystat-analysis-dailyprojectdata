package org.hackystat.dailyprojectdata.resource.codeissue;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.TestCase;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Tests that <code>CodeIssueRuntimeSorter</code> sorts by runtime correctly.
 * 
 * @author jsakuda
 */
public class TestCodeIssueRuntimeSorter extends TestCase {
  private static XMLGregorianCalendar runtime = Tstamp.makeTimestamp(Day.getInstance(2007, 11,
      3));
  private static XMLGregorianCalendar runtime2 = Tstamp.incrementHours(runtime, 5);

  private SensorData data;
  private SensorData data2;
  private SensorData data3;

  /** 
   * Sets up the fake sensor data.
   * 
   * @throws Exception Thrown if there are any errors during set up.
   */
  @Override
  protected void setUp() throws Exception {
    this.data = new SensorData();
    this.data.setRuntime(TestCodeIssueRuntimeSorter.runtime);
    this.data.setSensorDataType("CodeIssue");

    this.data2 = new SensorData();
    this.data2.setRuntime(TestCodeIssueRuntimeSorter.runtime2);
    this.data2.setSensorDataType("CodeIssue");

    this.data3 = new SensorData();
    this.data3.setRuntime(TestCodeIssueRuntimeSorter.runtime);
    this.data3.setSensorDataType("CodeIssue");
  }

  /** Tests that data gets added correctly. */
  public void testAddCodeIssueData() {
    CodeIssueRuntimeSorter sorter = new CodeIssueRuntimeSorter();
    sorter.addCodeIssueData(this.data);
    sorter.addCodeIssueData(this.data2);
    sorter.addCodeIssueData(this.data3);

    XMLGregorianCalendar lastRuntime = sorter.getLastRuntime();
    assertEquals("Runtimes should match.", TestCodeIssueRuntimeSorter.runtime2, lastRuntime);

    List<SensorData> lastCodeIssueBatch = sorter.getLastCodeIssueBatch();
    assertSame("Batch should only have one issue.", 1, lastCodeIssueBatch.size());
    assertSame("Data2 should be in the issue batch.", this.data2, lastCodeIssueBatch.get(0));
  }
}
