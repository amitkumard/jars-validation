
package com.ibm.lift.util;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.io.File;

public class GenerateChecksum {

    public static String getMd5Checksum(String datafile) throws Exception {

    MessageDigest md_md5 = MessageDigest.getInstance("MD5");
    FileInputStream fis = new FileInputStream(datafile);
    byte[] dataBytes = new byte[1024];


    int nread = 0;

    while ((nread = fis.read(dataBytes)) != -1) {

      md_md5.update(dataBytes, 0, nread);
    };


    byte[] mdbytes_md5 = md_md5.digest();

    //convert the byte to hex format

    StringBuffer sb_md5 = new StringBuffer("");
    for (int i = 0; i < mdbytes_md5.length; i++) {
    	sb_md5.append(Integer.toString((mdbytes_md5[i] & 0xff) + 0x100, 16).substring(1));
    }



    return sb_md5.toString();

  }

  public static String getSHA1Checksum(String datafile) throws Exception {

  MessageDigest md_sha = MessageDigest.getInstance("SHA1");
  FileInputStream fis = new FileInputStream(datafile);
  byte[] dataBytes = new byte[1024];

  int nread = 0;

  while ((nread = fis.read(dataBytes)) != -1) {
    md_sha.update(dataBytes, 0, nread);

  };

  byte[] mdbytes_sha = md_sha.digest();


  //convert the byte to hex format
  StringBuffer sb_sha = new StringBuffer("");
  for (int i = 0; i < mdbytes_sha.length; i++) {
    sb_sha.append(Integer.toString((mdbytes_sha[i] & 0xff) + 0x100, 16).substring(1));
  }

  return sb_sha.toString();

}

}
