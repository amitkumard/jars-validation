package com.ibm.lift;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.ibm.lift.util.JSONUtils;
import com.ibm.lift.util.GenerateChecksum;
import com.ibm.lift.util.HttpClientHelper;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;


public class RefreshJars {

  static String FILES = "FILES";
  static String NAME = "NAME";
  static String VERSION = "VERSION";
  static String CHECKSUM = "CHECKSUM";
  static String SIZE = "SIZE";
  static String URL = "URL";
  static String DIRECTORY = "../libs";


public static void main(String[] args) {

      JSONParser parser = new JSONParser();
      JSONUtils jsonUtil = new JSONUtils();

      System.out.println ("Refreshing local jars with latest updates...");

      System.out.println("Writing to remotemetafile!");
      HttpClientHelper.requestMetafile();
      System.out.println("Finished Writing!");

      try {
            Object obj1 = parser.parse(new FileReader("../metafile.json"));
            Object obj2 = parser.parse(new FileReader("../remotemetafile.json"));
            //check if both metafiles are same instead looking at individual entry

            if(jsonUtil.areEqual(obj1,obj2))
            {
              System.out.println("Nothing to refresh...");
              System.out.println("Data migration can begin");
            }
            else
            {
              System.out.println("Difference in metafile, pulling new version of jars...");
              compareMetafiles((JSONObject)obj1, (JSONObject)obj2);
            }

          }
          catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private static void compareMetafiles(JSONObject local, JSONObject remote)
    {
        JSONArray files = (JSONArray)remote.get(RefreshJars.FILES);
        int index = 0;
        for (Object file : files )
        {
          JSONObject remoteFile = (JSONObject)file;
          String remoteName = (String)remoteFile.get(RefreshJars.NAME);
          JSONObject localFile = searchMetaFile(local,remoteName);
          if(localFile != null)
          {
            boolean compareVersionFlag = compareValues (RefreshJars.VERSION,localFile,remoteFile);
            if(compareVersionFlag)
              System.out.println ("Version same for " + remoteName);
            else
            {
              System.out.println ("Version mismatch for " + remoteName + " downloading latest version...");
              HttpClientHelper.downloadJAR((String)remoteFile.get(RefreshJars.URL), RefreshJars.DIRECTORY, remoteName);
              try {
                boolean compareChecksumFlag = compareChecksum(remoteName,remoteFile);
                if(compareChecksumFlag)
                {
                  System.out.println("Checksum matched, Update metafile");
                  updateMetaFile(localFile,remoteFile);

                }
              }
              catch(Exception e) {
                e.printStackTrace();
              }
            }
          }
          else
          {
            System.out.println("Jar details not found locally, pulling the jar " + remoteName );
            HttpClientHelper.downloadJAR((String)remoteFile.get(RefreshJars.URL), RefreshJars.DIRECTORY, remoteName);
            try {
              boolean compareChecksumFlag = compareChecksum(remoteName,remoteFile);
              if(compareChecksumFlag)
              {
                System.out.println("Checksum matched, Update metafile");
                updateMetaFile(localFile,remoteFile);

              }
            }
            catch(Exception e) {
              e.printStackTrace();
            }
          }

          index ++;

        }
    }

    private static JSONObject searchMetaFile(JSONObject local,String remoteName)
    {
      JSONArray files = (JSONArray)local.get(RefreshJars.FILES);
      for (Object file : files )
      {
        JSONObject fileJson = (JSONObject)file;
        String localName = (String)fileJson.get(RefreshJars.NAME);
        if(localName.equals(remoteName))
          return (JSONObject) file;
      }
      return null;
    }

    private static boolean compareValues (String key, JSONObject localFile, JSONObject remoteFile)
    {
        String localValue = (String)localFile.get(key);
        String remoteValue = (String)remoteFile.get(key);
        return localValue.equals(remoteValue);
    }

    private static boolean compareChecksum(String remoteName,JSONObject remoteFile) throws Exception
    {
        String localChecksum = GenerateChecksum.getMd5Checksum(remoteName);
        String remoteChecksum = (String)remoteFile.get(RefreshJars.CHECKSUM);
        return localChecksum.equals(remoteChecksum);
    }

    private static void updateMetaFile(JSONObject localFile,JSONObject remoteFile) throws IOException
    {
        FileWriter file = null;
        try {
        JSONParser parser = new JSONParser();
        JSONObject local = (JSONObject) parser.parse(new FileReader("../metafile.json"));
        JSONArray filesArray = (JSONArray)local.get(RefreshJars.FILES);
        filesArray.remove(localFile);
        filesArray.add(remoteFile);
        JSONObject rootElement = new JSONObject();
        rootElement.put("FILES",filesArray);
        file = new FileWriter("../metafile.json");
        file.write(JSONUtils.jsonFormatter(rootElement));
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      finally {
        file.flush();
        file.close();
      }
    }
}
