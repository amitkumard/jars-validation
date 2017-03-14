/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * class for converting Object to JSONObject and comparing to JSON Objects
 *
 */

public class JSONUtils {

  /**
   *
   * Checks if two JSONObjects are equal or not
   *
   * @param jarsmetafile
   *          The local metadata object
   * @param metafile
   *          The remote metadata object
   * @return
   *          Returns true if the objects are equal otherwise false
   * @throws JSONException
   *          Throws JSONException if there is an error in converting the Object to JSONObject
   */

  public static boolean areEqual(Object jarsmetafile, Object metafile) throws JSONException {
    Object obj1Converted = convertJsonElement(jarsmetafile);
    Object obj2Converted = convertJsonElement(metafile);
    return obj1Converted.equals(obj2Converted);
  }

  /**
   *
   * Converts an Object to a JSONObject
   *
   * @param elem
   * Object that is to be converted to JSONObject
   * @return
   * Returns the input Object elem as a JSONObject
   * @throws JSONException
   * Throws JSONException if the input Object elem doesn't have a valid JSON structure
   */

  private static Object convertJsonElement(Object elem) throws JSONException {
    if (elem instanceof JSONObject) {
      JSONObject obj = (JSONObject) elem;
      Iterator<String> keys = obj.keys();
      Map<String, Object> jsonMap = new HashMap<>();
      while (keys.hasNext()) {
        String key = keys.next();
        jsonMap.put(key, convertJsonElement(obj.get(key)));
      }
      return jsonMap;
    } else if (elem instanceof JSONArray) {
      JSONArray arr = (JSONArray) elem;
      Set<Object> jsonSet = new HashSet<>();
      for (int i = 0; i < arr.length(); i++) {
        jsonSet.add(convertJsonElement(arr.get(i)));
      }
      return jsonSet;
    } else {
      return elem;
    }

  }

  /**
   *
   * Pretty prints the JSONObject as a string
   *
   * @param object
   * JSONObject
   * @return
   * Returns a string that is beautified as a JSON
   * @throws Exception
   * Throws Exception if there is an error in printing the JSONObject
   */

  public static String jsonFormatter(org.json.simple.JSONObject object) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
  }

}
