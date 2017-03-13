/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit.util;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;

/**
 * 
 * class for assigning values to the localMeta, remoteMeta, tmpDirectory variables,
 * reading file and deleting directory
 *
 */

public class FileUtil {

  public static String localMeta;
  public static String remoteMeta;
  public static String tmpDirectory;

  //TODO: Add logic for creating hidden directory for Windows platform
  /**
   * 
   * Assigns values to the variables localMeta, remoteMeta and tmpDirectory based on the
   * OS platform
   * 
   */
  
  public static void getMetaFileNames() {
    OsUtils.OSType detectedOS = OsUtils.getOperatingSystemType();
    switch (detectedOS.name()) {
      case "Windows":
        localMeta = "../metafile.json";
        remoteMeta = "../remotemetafile.json";
        break;
      case "MacOS":
        localMeta = "../.metafile.json";
        remoteMeta = "../.remotemetafile.json";
        tmpDirectory = "../.tmp";
        break;
      case "Linux":
        localMeta = "../.metafile.json";
        remoteMeta = "../.remotemetafile.json";
        tmpDirectory = "../.tmp";
        break;
      case "Other":
        break;
    }
  }

  /**
   * 
   * Reads the contents of the files
   * 
   * @param path
   * The file to be read
   * @param encoding
   * Encoding to be used 
   * @return
   * Returns the contents of the file as a string
   * @throws IOException
   * Throws IOException if there is an error in reading the file or if the file is not found
   */
  
  public static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  /**
   * 
   * Deletes the directory if the directory has no files. If the directory has files
   * it deletes all the files in the directory and then the directory gets delted
   * 
   * @param file
   * The file to be deleted
   * @throws IOException
   * Throws IOException if there isn't any directory
   */
  
  public static void deleteDirectory(File file) throws IOException {
    if (file.isDirectory()) {
      if (file.list().length == 0) {
        file.delete();
      } else {
        String [] files = file.list();
        for (String temp : files) {
          File fileDelete = new File(file, temp);
          deleteDirectory(fileDelete);
        }

        if (file.list().length == 0) {
          file.delete();
        }
      }

    } else {
      file.delete();
    }
  }
}
