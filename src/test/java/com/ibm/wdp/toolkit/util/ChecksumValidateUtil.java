/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */

package com.ibm.wdp.toolkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class ChecksumValidateUtil {
  static String directory = "../libs";
  public static boolean generateChecksum() {
    boolean success = true; //set it to false when checksum generation is not a success

    File dir = new File(directory);
    File[] directoryListing = dir.listFiles();
    try {
      if (directoryListing != null) {
        for (File child : directoryListing) {
          String checksum = getMd5Checksum(child.getName());
          System.out.println(checksum);
          if (checksum == null || checksum.length() == 0) {
            success = false;
            break;
          }
        }
      } else {
        success = false;
      }
    } catch (Exception e) {
      success = false;
      e.printStackTrace();
    }

    return success;
  }

  public static String getMd5Checksum(String datafile) throws Exception {
    MessageDigest mdMd5 = MessageDigest.getInstance("MD5");
    FileInputStream fis = new FileInputStream(datafile);
    byte[] dataBytes = new byte[1024];

    int nread = 0;

    while ((nread = fis.read(dataBytes)) != -1) {
      mdMd5.update(dataBytes, 0, nread);
    }
    byte[] mdbytesMd5 = mdMd5.digest();
    //convert the byte to hex format
    StringBuffer sbMd5 = new StringBuffer("");
    for (int i = 0; i < mdbytesMd5.length; i++) {
      sbMd5.append(Integer.toString((mdbytesMd5[i] & 0xff) + 0x100, 16).substring(1));
    }

    return sbMd5.toString();
  }

}
