/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 * 
 * class for requesting remoteFileData from the server and downloading JARs
 *
 */

public class HttpClientHelper {

  private static final Logger LOGGER = Logger.getLogger(HttpClientHelper.class);
  private static final String URL = "https://liftcfappmaster.mybluemix.net";
  private static final String DIRECTORY = "../libs";

  /**
   * 
   * Makes a secure call to the downloader-server to obtain the remote metadata object
   * 
   * @param metaFileName
   * File to which the remote metadata object has to be written 
   */
  
  public static void requestMetafile(String metaFileName) {
    String data = null;
    String userAgent = "Mozilla/5.0";
    try {
      URL obj = new URL(URL);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      // optional default is GET
      con.setRequestMethod("GET");
      //add request header
      con.setRequestProperty("User-Agent", userAgent);

      int responseCode = con.getResponseCode();
      System.out.println("Sending 'GET' request to URL : " + URL);
      System.out.println("Response Code : " + responseCode);

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      data = EncryptionUtil.encrpytString(response.toString());
      try {
        FileWriter file = new FileWriter(metaFileName);
        file.write(data);
        file.flush();
        file.close();
      } catch (Exception e) {
        System.out.println("Failed to write to file");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * Makes a secure request to the server where the requested JAR resides and downloads the JAR
   * 
   * @param fileURL
   * The URL where the JAR to be downloaded resides
   * @param saveDir
   * The directory where the downloaded JAR is to be copied
   * @param fileName
   * The name of the JAR that is to be downloaded
   */
  
  public static void downloadJAR(String fileURL, String saveDir, String fileName) {
    int bufferSize = 4096;
    try {
      URL url = new URL(fileURL);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      int responseCode = httpConn.getResponseCode();

      // always check HTTP response code first
      if (responseCode == HttpURLConnection.HTTP_OK) {

        String disposition = httpConn.getHeaderField("Content-Disposition");
        String contentType = httpConn.getContentType();
        int contentLength = httpConn.getContentLength();

        if (disposition != null) {
          // extracts file name from header field
          int index = disposition.indexOf("filename=");
          if (index > 0) {
            fileName = disposition.substring(index + 10,
            disposition.length() - 1);
          }
        } else {
          // extracts file name from URL
          fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
          fileURL.length());
        }

        LOGGER.debug("Content-Type = " + contentType);
        LOGGER.debug("Content-Disposition = " + disposition);
        LOGGER.debug("Content-Length = " + contentLength);
        LOGGER.debug("fileName = " + fileName);

        // opens input stream from the HTTP connection
        InputStream inputStream = httpConn.getInputStream();
        String saveFilePath = saveDir + File.separator + fileName;

        // opens an output stream to save into file
        FileOutputStream outputStream = new FileOutputStream(saveFilePath);

        int bytesRead = -1;
        byte[] buffer = new byte[bufferSize];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        System.out.println(fileName + " File downloaded");
        System.out.println("Moving the file to libs");
        MoveFileUtil.moveJAR(fileName, HttpClientHelper.DIRECTORY, saveDir);
      } else {
        System.out.println("No file to download. Server replied HTTP code: " + responseCode);
      }
      httpConn.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
