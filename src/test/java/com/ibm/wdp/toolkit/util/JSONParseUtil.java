/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */

package com.ibm.wdp.toolkit.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JSONParseUtil {
  static String url = "https://liftcfappmaster.mybluemix.net";
  static JSONParser parser = new JSONParser();

  public static boolean isJSONParsable() {
    boolean parsable = false; //set it to true if parsable
    String data = requestServer();
    if (data == null) {
      parsable = false;
    } else {
      try {
        Object object = parser.parse(data);
        object = convertJsonElement(object);
        if (object == null) {
          parsable = false;
        } else {
          parsable = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
        parsable = false;
      }
    }
    return parsable;
  }

  public static String requestServer() {
    String userAgent = "Mozilla/5.0";
    String data = null;
    int responseCode = 0;
    try {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      // optional default is GET
      con.setRequestMethod("GET");
      //add request header
      con.setRequestProperty("User-Agent", userAgent);
      responseCode = con.getResponseCode();
      System.out.println("Sending 'GET' request to URL : " + url);
      System.out.println("Response Code : " + responseCode);
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      data = response.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

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
}
