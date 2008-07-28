package org.hackystat.dailyprojectdata.resource.commit;

import java.util.List;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * The class wrapping SensorData instance, which provides easy access to the
 * commit specific properties.
 * @author aito
 * 
 */
public class CommitData {
  /** The wrapped data instance. */
  private final SensorData data;

  /**
   * Constructs this object with the specified SensorData instance.
   * @param data the specified data instance.
   */
  public CommitData(SensorData data) {
    this.data = data;
  }

  /**
   * Returns the owner of the wrapped data instance.
   * @return the data owner.
   */
  public String getOwner() {
    return this.data.getOwner();
  }

  /**
   * Returns the total lines added stored in this data instance.
   * @return the total lines added.
   */
  public int getLinesAdded() {
    return Integer.valueOf(this.getCommitProperty("linesAdded").getValue());
  }

  /**
   * Returns the total lines deleted stored in this data instance.
   * @return the total lines deleted.
   */
  public int getLinesDeleted() {
    return Integer.valueOf(this.getCommitProperty("linesDeleted").getValue());
  }

  /**
   * Returns the total lines modified in this data instance.
   * Note that linesModified is an optional property, so this will return 0 if not present. 
   * @return the total lines modified
   */
  public int getLinesModified() {
    return Integer.valueOf(this.getCommitProperty("linesModified", "0").getValue());
  }

  /**
   * Returns true if the specified object equals this object.
   * @param object the object to test.
   * @return true if equal, false if not.
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof CommitData)) {
      return false;
    }

    CommitData otherData = (CommitData) object;
    return this.data.equals(otherData.data);
  }

  /**
   * Returns the hashcode of this object.
   * @return the hashcode.
   */
  @Override
  public int hashCode() {
    int result = 17;
    result = 37 * result + this.data.hashCode();
    return result;
  }

  /**
   * Returns the Property instance with the specified property name. If no
   * property exists, null is returned.
   * @param propertyName the property name to search for.
   * @return the property with the specified name or null.
   */
  public Property getCommitProperty(String propertyName) {
    List<Property> propertyList = this.data.getProperties().getProperty();
    for (Property property : propertyList) {
      if (propertyName.equals(property.getKey())) {
        return property;
      }
    }
    return null;
  }
  
  /**
   * Returns the Property instance with the specified property name. If no
   * property exists, defaultValue is returned. 
   * @param propertyName the property name to search for.
   * @param defaultValue The string to return if the property does not exist. 
   * @return The property with the specified name or defaultValue. 
   */
  public Property getCommitProperty(String propertyName, String defaultValue) {
    List<Property> propertyList = this.data.getProperties().getProperty();
    for (Property property : propertyList) {
      if (propertyName.equals(property.getKey())) {
        return property;
      }
    }
    // Make a new Property instance.
    Property property = new Property();
    property.setKey(propertyName);
    property.setValue(defaultValue);
    return property;
  }

  /**
   * Returns the string representation of this data object, which is useful for
   * debugging purposes.
   * @return the string representation.
   */
  @Override
  public String toString() {
    return "Owner=" + this.getOwner() + ", LinesAdded=" + this.getLinesAdded()
        + ", LinesDeleted=" + this.getLinesDeleted() + ", LinesModified="
        + this.getLinesModified();
  }
}
