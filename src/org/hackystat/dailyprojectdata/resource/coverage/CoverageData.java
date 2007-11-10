package org.hackystat.dailyprojectdata.resource.coverage;

import java.util.List;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * The class wrapping SensorData instance, which provides easy access to the
 * coverage specific properties.
 * @author aito
 * 
 */
public class CoverageData {
  /** The wrapped data instance. */
  private final SensorData data;
  /** The property name of the "uncovered" coverage values. */
  private static final String UNCOVERED_NAME = "Uncovered";
  /** The property name of the "covered" coverage values. */
  private static final String COVERED_NAME = "Covered";

  /**
   * Constructs this object with the specified SensorData instance.
   * @param data the specified data instance.
   */
  public CoverageData(SensorData data) {
    this.data = data;
  }

  /**
   * Returns the resource of the wrapped data instance.
   * @return the data resource.
   */
  public String getResource() {
    return this.data.getResource();
  }

  /**
   * Returns the owner of the wrapped data instance.
   * @return the data owner.
   */
  public String getOwner() {
    return this.data.getOwner();
  }

  /**
   * Returns the uncovered coverage value.
   * @return the uncovered value.
   */
  public int getUncovered() {
    return Double.valueOf(this.getCoverageProperty(UNCOVERED_NAME).getValue()).intValue();
  }

  /**
   * Returns the covered coverage value.
   * @return the covered value.
   */
  public int getCovered() {
    return Double.valueOf(this.getCoverageProperty(COVERED_NAME).getValue()).intValue();
  }

  /**
   * Returns true if the specified object equals this object.
   * @param object the object to test.
   * @return true if equal, false if not.
   */
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof CoverageData)) {
      return false;
    }

    CoverageData otherData = (CoverageData) object;
    return this.data.equals(otherData.data);
  }

  /**
   * Returns the hashcode of this object.
   * @return the hashcode.
   */
  public int hashCode() {
    int result = 17;
    result = 37 * result + this.data.hashCode();
    return result;
  }

  /**
   * Returns the Property instance with the specified property name. If no
   * property exists, false is returned.
   * @param propertyName the property name to search for.
   * @return the property with the specified name or null.
   */
  public Property getCoverageProperty(String propertyName) {
    List<Property> propertyList = this.data.getProperties().getProperty();
    for (Property property : propertyList) {
      if (propertyName.equals(property.getKey())) {
        return property;
      }
    }
    return null;
  }

  /**
   * Returns the string representation of this object, which is useful for
   * debugging purposes.
   * @return the string representation.
   */
  public String toString() {
    return "Owner=" + this.getOwner() + ", Resource=" + this.getResource() + ", Covered="
        + this.getCovered() + ", Uncovered=" + this.getUncovered();
  }
}
