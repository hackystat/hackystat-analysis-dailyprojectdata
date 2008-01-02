package org.hackystat.dailyprojectdata.resource.commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the CommitDataContainer stores and returns the correct data based on
 * the wrapped data instances.
 * @author aito
 * 
 */
public class TestCommitDataContainer {
  /** The fields used to test the CoverageData instance. */
  private CommitDataContainer data = new CommitDataContainer();
  private String userAusten = "austen@hawaii.edu";
  private String userJulie = "jsakuda@hawaii.edu";
  private String userAaron = "kagawaa@hawaii.edu";

  /** Setup the CoverageData that is tested. */
  @Before
  public void setUp() {
    // First, create the test SensorData instances.
    XMLGregorianCalendar runtimeCalendar = Tstamp.makeTimestamp(new Date().getTime());
    String runtimeString = runtimeCalendar.toString();
    SensorData sensorDataAusten01 = TestCommitData.createData(runtimeString, runtimeString,
        userAusten, "C:\\foo.java", "10", "100");
    SensorData sensorDataAusten02 = TestCommitData.createData(runtimeString, runtimeString,
        userAusten, "C:\\foo2.java", "11", "12");
    SensorData sensorDataJulie01 = TestCommitData.createData(runtimeString, runtimeString,
        userJulie, "C:\\foo3.java", "10", "1");
    SensorData sensorDataJulie02 = TestCommitData.createData(runtimeString, runtimeString,
        userJulie, "C:\\foo4.java", "12", "100");
    SensorData sensorDataAaron01 = TestCommitData.createData(runtimeString, runtimeString,
        userAaron, "C:\\foo5.java", "99", "33");

    // Second, adds the sensor.
    this.data.addCommitData(sensorDataAusten01);
    this.data.addCommitData(sensorDataAusten02);
    this.data.addCommitData(sensorDataJulie01);
    this.data.addCommitData(sensorDataJulie02);
    this.data.addCommitData(sensorDataAaron01);
  }

  /** Tests if the correct amount of lines added are returned. */
  @Test
  public void testGetLinesAdded() {
    assertEquals("The total lines added is incorrect.", 21, this.data
        .getLinesAdded(this.userAusten));
  }

  /** Tests if the correct amount of lines deleted are returned. */
  @Test
  public void testGetLinesDeleted() {
    assertEquals("The total lines deleted is incorrect.", 112, this.data
        .getLinesDeleted(this.userAusten));
  }


  /** Tests if the correct amount of commits by a user is returned. */
  @Test
  public void testGetCommits() {
    assertEquals("The total commits is incorrect.", 2, this.data.getCommits(this.userAusten));
    Assert.assertEquals("The total commits is incorrect.", 0, this.data
        .getCommits("Non-ExistentUser"));
  }

  /** Tests if the correct owners of the wrapped SensorData are returned. */
  @Test
  public void testGetOwners() {
    List<String> owners = this.data.getOwners();
    assertEquals("The amount of owners is incorrect.", 3, owners.size());
    assertTrue("The owner " + userAusten + " does not exist.", owners
        .contains("austen@hawaii.edu"));
    assertTrue("The owner " + userJulie + " does not exist.", owners
        .contains("jsakuda@hawaii.edu"));
    assertTrue("The owner " + userAaron + " does not exist.", owners
        .contains("kagawaa@hawaii.edu"));
  }

  /** Tests if the all data entries are returned. */
  @Test
  public void testGetData() {
    assertEquals("The total amount of data entries returned is incorrect.", 5, this.data
        .getData().size());
  }
}
