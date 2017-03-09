package com.ibm.lift.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringWriter;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class JSONUtils {

  public static boolean areEqual(Object jarsmetafile, Object metafile) throws JSONException
  {
    Object obj1Converted = convertJsonElement(jarsmetafile);
    Object obj2Converted = convertJsonElement(metafile);
    return obj1Converted.equals(obj2Converted);
  }

  private static Object convertJsonElement(Object elem) throws JSONException
  {
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

 public static String jsonFormatter(org.json.simple.JSONObject object) throws Exception
 {
   ObjectMapper mapper = new ObjectMapper();
   return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
 }

}
