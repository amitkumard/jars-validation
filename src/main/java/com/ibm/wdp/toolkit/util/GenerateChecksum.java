/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit.util;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class GenerateChecksum {
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


    fis.close();
    return sbMd5.toString();
   

  }

  public static String getSHA1Checksum(String datafile) throws Exception {

    MessageDigest mdSHA = MessageDigest.getInstance("SHA1");
    FileInputStream fis = new FileInputStream(datafile);
    byte[] dataBytes = new byte[1024];

    int nread = 0;

    while ((nread = fis.read(dataBytes)) != -1) {
      mdSHA.update(dataBytes, 0, nread);
    }

    byte[] mdbytesSHA = mdSHA.digest();


  //convert the byte to hex format
    StringBuffer sbSHA = new StringBuffer("");
    for (int i = 0; i < mdbytesSHA.length; i++) {
      sbSHA.append(Integer.toString((mdbytesSHA[i] & 0xff) + 0x100, 16).substring(1));
    }
    fis.close();
    return sbSHA.toString();

  }

}
