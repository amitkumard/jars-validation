/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit.util;

import java.util.Locale;

public class OsUtils {
  /**
   * types of Operating Systems
   */

  public enum OSType {
    Windows, MacOS, Linux, Other
  };

  // cached result of os detection
  protected static OSType detectedos;

  /**
   * detect the operating system from the os.name System property and cache
   * the result
   *
   * @return
   * Returns the operating system detected
   */

  public static OSType getOperatingSystemType() {
    if (detectedos == null) {
      String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
        detectedos = OSType.MacOS;
      } else if (os.indexOf("win") >= 0) {
        detectedos = OSType.Windows;
      } else if (os.indexOf("nux") >= 0) {
        detectedos = OSType.Linux;
      } else {
        detectedos = OSType.Other;
      }
    }
    return detectedos;
  }
}
