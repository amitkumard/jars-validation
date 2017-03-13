/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * class for moving files (JARs) from the temp directory to the libs directory
 *
 */

public class MoveFileUtil {
  
  /**
   * 
   * Moves the file from tempdir directory to the dir directory
   * 
   * @param filename
   * The name of the file to be moved
   * @param dir
   * The name of the directory (libs) to which the file has to be moved
   * @param tempdir
   * The name of the directory (temp) from which the file has to be moved
   */
	
  public static void moveJAR(String filename, String dir, String tempdir) {
    InputStream inStream = null;
    OutputStream outStream = null;
    try {
      File afile = new File(tempdir + File.separator + filename);
      File bfile = new File(dir + File.separator + filename);
      inStream = new FileInputStream(afile);
      outStream = new FileOutputStream(bfile);
      byte[] buffer = new byte[1024];
      int length;
      //copy the file content in bytes
      while ((length = inStream.read(buffer)) > 0) {
        outStream.write(buffer, 0, length);
      }

      inStream.close();
      outStream.close();
      //delete the original file
      afile.delete();
      System.out.println("File copy successful!");
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("File copy failed!");
    }
  }
}