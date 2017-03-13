/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.log4j.Logger;
import java.nio.charset.Charset;


import com.ibm.wdp.toolkit.util.JSONUtils;
import com.ibm.wdp.toolkit.util.GenerateChecksum;
import com.ibm.wdp.toolkit.util.HttpClientHelper;
import com.ibm.wdp.toolkit.util.EncryptionUtil;
import com.ibm.wdp.toolkit.util.FileUtil;

/**
 *
 * downloader-client main class for downloading toolkit jars
 *
 */

public class RefreshJars {

  private static final String FILES = "FILES";
  private static final String NAME = "NAME";
  private static final String VERSION = "VERSION";
  private static final String CHECKSUM = "CHECKSUM";
  private static final String SIZE = "SIZE";
  private static final String URL = "URL";
  private static final String DIRECTORY = "../libs";
  private static final String TEMP_DIRECTORY = "../temp";
  private static boolean localMetaFound = Boolean.FALSE;
  private static final Logger LOGGER = Logger.getLogger(RefreshJars.class);


  public static void main(String[] args) {
    FileUtil.getMetaFileNames();
    JSONParser parser = new JSONParser();
    String content = "";
    String decryptedContents = "";
    System.out.println("Refreshing local jars with latest updates...");
    LOGGER.debug("------------------------------------------------");
    LOGGER.debug("  Refreshing local jars with latest updates  ");
    LOGGER.debug("------------------------------------------------");
    LOGGER.debug("Writing to remotemetafile!");
    HttpClientHelper.requestMetafile(FileUtil.remoteMeta);
    LOGGER.debug("Finished Writing!");
    try {
      Object obj1 = null;
      if (new File(FileUtil.localMeta).isFile()) {
        content = FileUtil.readFile(FileUtil.localMeta, Charset.defaultCharset());
        decryptedContents = EncryptionUtil.decrpytString(content);
        LOGGER.debug("Local metadata file");
        LOGGER.debug(decryptedContents);
        obj1 = parser.parse(decryptedContents);
        localMetaFound = Boolean.TRUE;
      }
      content = FileUtil.readFile(FileUtil.remoteMeta, Charset.defaultCharset());
      decryptedContents = EncryptionUtil.decrpytString(content);
      LOGGER.debug("Remote metadata file");
      LOGGER.debug(decryptedContents);
      Object obj2 = parser.parse(decryptedContents);
      //check if both metafiles are same instead looking at individual entry
      if (localMetaFound) {
        if (JSONUtils.areEqual(obj1, obj2)) {
          System.out.println("Nothing to refresh...");
          System.out.println("Data migration can begin");
        } else {
          System.out.println("Difference in metafile, pulling new version of jars...");
          compareMetafiles((JSONObject) obj1, (JSONObject) obj2);
        }
      } else {
        System.out.println("Difference in metafile, pulling new version of jars...");
        compareMetafiles((JSONObject) obj1, (JSONObject) obj2);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param local
   *           Local metadata object that is within toolkit
   * @param remote
   *           Remote metadata object response received from downloder-server
   * @throws IOException
   *           If metadata file is not found
   */

  private static void compareMetafiles(JSONObject local, JSONObject remote) throws IOException {

    File tmpDir = new File(FileUtil.tmpDirectory);
    if (!tmpDir.exists())
      tmpDir.mkdir();
    else
      FileUtil.deleteDirectory(tmpDir);
    if (tmpDir.exists())
      LOGGER.debug("Temporary Directory for moving latest JARS created");
    JSONArray files = (JSONArray) remote.get(RefreshJars.FILES);
    int index = 0;
    for (Object file : files) {
      JSONObject remoteFile = (JSONObject) file;
      String remoteName = (String) remoteFile.get(RefreshJars.NAME);
      JSONObject localFile = searchMetaFile(local, remoteName);
      if (localFile != null) {
        boolean compareVersionFlag = compareValues(RefreshJars.VERSION, localFile, remoteFile);
        if (compareVersionFlag) {
          System.out.println("Version same for " + remoteName);
        } else {
          System.out.println("Version mismatch for " + remoteName + " downloading latest version...");
          HttpClientHelper.downloadJAR((String) remoteFile.get(RefreshJars.URL), FileUtil.tmpDirectory, remoteName);
          try {
            boolean compareChecksumFlag = compareChecksum(remoteName, remoteFile);
            if (compareChecksumFlag) {
              System.out.println("Checksum matched, Update metafile");
              updateMetaFile(localFile, remoteFile);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } else {
        System.out.println("Jar details not found locally, pulling the jar " + remoteName);
        HttpClientHelper.downloadJAR((String) remoteFile.get(RefreshJars.URL), FileUtil.tmpDirectory, remoteName);
        try {
          boolean compareChecksumFlag = compareChecksum(remoteName, remoteFile);
          if (compareChecksumFlag) {
            System.out.println("Checksum matched, Update metafile");
            updateMetaFile(localFile, remoteFile);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      index++;
    }
    FileUtil.deleteDirectory(tmpDir);
    if (!tmpDir.exists())
      LOGGER.debug("Temporary directory deleted!!");
    else
      LOGGER.debug("Failed deleting temporary directory");
  }

  /**
   *
   * @param local
   *           JSONObject of local metadata file that has to be searched against remote metafile
   * @param remoteName
   *            Name of the metadata attribute that needs to be searched
   * @return
   *         returns JSONObject if remoteName found within local metadata file
   */
  private static JSONObject searchMetaFile(JSONObject local, String remoteName) {
    if (local == null)
      return local;
    JSONArray files = (JSONArray) local.get(RefreshJars.FILES);
    for (Object file : files) {
      JSONObject fileJson = (JSONObject) file;
      String localName = (String) fileJson.get(RefreshJars.NAME);
      if (localName.equals(remoteName))
        return (JSONObject) file;
    }
    return null;
  }

  /**
   *
   * @param key
   *          Key to compare
   * @param localFile
   *          Local metadata object that is within toolkit
   * @param remoteFile
   *           Remote metadata object response received from downloder-server
   * @return
   *        Returns true if values are matching, else false
   *
   */
  private static boolean compareValues(String key, JSONObject localFile, JSONObject remoteFile) {
    String localValue = (String) localFile.get(key);
    String remoteValue = (String) remoteFile.get(key);
    return localValue.equals(remoteValue);
  }

  /**
   *
   * @param remoteName
   *            JAR file name available in remote metadata object response received from downloder-server
   * @param remoteFile
   *            Remote metadata object response received from downloder-server
   * @return
   *        Returns true if checksum matches, else false
   * @throws Exception
   *        If error in computing checksum
   */
  private static boolean compareChecksum(String remoteName, JSONObject remoteFile) throws Exception {
    String localChecksum = GenerateChecksum.getMd5Checksum(remoteName);
    String remoteChecksum = (String) remoteFile.get(RefreshJars.CHECKSUM);
    return localChecksum.equals(remoteChecksum);
  }

  /**
   *
   * @param localFile
   *             Local metadata object that is within toolkit
   * @param remoteFile
   *             Remote metadata object response received from downloder-server
   * @throws IOException
   *             If metadata file is not found or cannot be updated
   */
  private static void updateMetaFile(JSONObject localFile, JSONObject remoteFile) throws IOException {
    FileWriter file = null;
    JSONObject local = null;
    JSONArray filesArray = null;
    JSONParser parser = new JSONParser();
    try {
      if (new File(FileUtil.localMeta).isFile()) {
        String content = FileUtil.readFile(FileUtil.localMeta, Charset.defaultCharset());
        String decryptedContents = EncryptionUtil.decrpytString(content);
        local = (JSONObject) parser.parse(decryptedContents);
        filesArray = (JSONArray) local.get(RefreshJars.FILES);
      } else {
        filesArray = new JSONArray();
      }
      filesArray.remove(localFile);
      filesArray.add(remoteFile);
      JSONObject rootElement = new JSONObject();
      rootElement.put("FILES", filesArray);
      file = new FileWriter(FileUtil.localMeta);
      file.write(EncryptionUtil.encrpytString(rootElement.toString()));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      file.flush();
      file.close();
    }
  }
}
