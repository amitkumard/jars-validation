package com.ibm.lift.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.InputStream;

public class HttpClientHelper {

  static String url = "http://cfappmaster.mybluemix.net/";

  public static void requestMetafile()
  {
    String data = null;
    String USER_AGENT = "Mozilla/5.0";
      try
      {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
        {
          response.append(inputLine);
        }
        in.close();

        data = response.toString();
        //print result
        //System.out.println("Data Received: " + data);

        try
        {
            FileWriter file = new FileWriter("../remotemetafile.json");

            file.write(data);
            file.flush();
        }
        catch(Exception e)
        {
          System.out.println("Failed to write to file");
        }


      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
  }

  public static void downloadJAR(String fileURL, String saveDir, String fileName)
  {
      int BUFFER_SIZE = 4096;
    try
    {
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

          System.out.println("Content-Type = " + contentType);
          System.out.println("Content-Disposition = " + disposition);
          System.out.println("Content-Length = " + contentLength);
          System.out.println("fileName = " + fileName);

          // opens input stream from the HTTP connection
          InputStream inputStream = httpConn.getInputStream();
          String saveFilePath = saveDir + File.separator + fileName;

          // opens an output stream to save into file
          FileOutputStream outputStream = new FileOutputStream(saveFilePath);

          int bytesRead = -1;
          byte[] buffer = new byte[BUFFER_SIZE];
          while ((bytesRead = inputStream.read(buffer)) != -1) {
              outputStream.write(buffer, 0, bytesRead);
          }

          outputStream.close();
          inputStream.close();

          System.out.println(fileName + " File downloaded");
      } else {
          System.out.println("No file to download. Server replied HTTP code: " + responseCode);
      }
      httpConn.disconnect();
    }
    catch(Exception e)
    {

    }

  }
}
