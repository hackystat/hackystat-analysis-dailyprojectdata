package org.hackystat.dailyprojectdata.resource.codeissue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;

/**
 * IssueTypeCounter takes a set of CodeIssue SensorData all associated with a single tool,
 * and constructs a data structure that maps issue types to their aggregate counts 
 * across all tools.  
 * 
 * For example, consider two CodeIssue sensor data instances, illustrated below:
 * <pre>
 * [SensorData tool="Foo" sdt="CodeIssue" properties(["JavaDoc", "2"], ["Indentation", "3"])]
 * [SensorData tool="Foo" sdt="CodeIssue" properties(["JavaDoc", "4"], ["NPE", "3"])]
 * </pre>
 * 
 * We process these SensorData instances into the following IssueTypeCounter map:
 * <pre>
 * ["JavaDoc", 6]
 * ["Indentation", 3]
 * ["NPE", 3]
 * </pre>
 * 
 * @author Philip Johnson
 *
 */
public class IssueTypeCounter {
  
  /** Maps issue names to their number of occurrences across all sensor data instances. */
  private Map<String, Integer>type2count = new HashMap<String, Integer>();
  
  /**
   * Constructs the IssueTypeCounter from the passed set of SensorData instances.
   * @param dataSet The snapshot of sensor data instances for a given tool.
   * @param logger The logger to get errors.
   */
  public IssueTypeCounter (Set<SensorData> dataSet, Logger logger) {
    for (SensorData data : dataSet) {
      for (Property property : data.getProperties().getProperty()) {
        if (property.getKey().startsWith("Type_")) {
          String typeName = null;
          Integer typeNum = null;
          try {
            typeName = property.getKey().substring(5);
            typeNum = Integer.parseInt(property.getValue());
            // Now we have the type and value, so initialize our map if necessary.
            if (type2count.get(typeName) == null) {
              type2count.put(typeName, 0);
            }
            // now increment this type value by the newly found number of occurrences.
            type2count.put(typeName, typeNum + type2count.get(typeName));
          }
          catch (Exception e) {
            logger.info("Problem with: " + typeName + " " + typeNum);
          }
        }
      }
    }
  }
  
  /**
   * Get the set of all types found from processing the sensor data snapshot.
   * @return The set of CodeIssue types.
   */
  public Set<String> getTypes() {
    return type2count.keySet();
  }
  
  /**
   * Return the number of CodeIssues of the given type found in this snapshot. 
   * @param type The CodeIssue type.
   * @return The count, or zero if this type was not found.
   */
  public int getCount(String type) {
    Integer count = type2count.get(type);
    return (count == null) ? 0 : count;
  }
  
  

}
