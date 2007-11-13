package org.hackystat.dailyprojectdata.resource.codeissue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Counts the number of occurrences each tool/type pair occurs. 
 * 
 * @author jsakuda
 */
public class ToolToTypeCounter {
  private Map<String, Map<String, Integer>> toolTypeMap;

  /** Creates and initializes a new counter. */
  public ToolToTypeCounter() {
    this.toolTypeMap = new HashMap<String, Map<String, Integer>>();
  }
  
  /**
   * Adds the number of occurrences of the given tool/type pair to the existing count.
   * 
   * @param tool The tool.
   * @param type The type.
   * @param numberOfOccurrences The number of times the tool/type pair occurred.
   */
  public void add(String tool, String type, Integer numberOfOccurrences) {
    if (!toolTypeMap.containsKey(tool)) {
      toolTypeMap.put(tool, new HashMap<String, Integer>());
    }
    
    if (type != null) {
      Map<String, Integer> toolMap = toolTypeMap.get(tool);
      if (toolMap.containsKey(type)) {
        Integer count = toolMap.get(type);
        toolMap.put(type, count + numberOfOccurrences);
      }
      else {
        // no previous mapping
        toolMap.put(type, numberOfOccurrences);
      }
    }
  }
  
  /**
   * Gets the type counts for the given tool.
   * 
   * @param tool The tool to get the type counts for.
   * @return Returns a mapping of types to the number of occurrences of that type.
   */
  public Map<String, Integer> getTypeCounts(String tool) {
    return this.toolTypeMap.get(tool);
  }
  
  /**
   * Gets all the tools appearing in the counter.
   * 
   * @return Returns the set of tools in the counter.
   */
  public Set<String> getTools() {
    return this.toolTypeMap.keySet();
  }
}
