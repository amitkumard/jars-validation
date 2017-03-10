package com.ibm.lift.util;

import java.nio.charset.Charset;
import java.nio.file.*;
import java.io.IOException;

public class FileUtil {

  public static String localMeta;
  public static String remoteMeta;

  public static void getMetaFileNames()
  {
    OsUtils.OSType detectedOS = OsUtils.getOperatingSystemType();
    switch (detectedOS.name()) {
        case "Windows":
          localMeta = "../metafile.json";
          remoteMeta = "../remotemetafile.json";
          break;
        case "MacOS":
          localMeta = "../.metafile.json";
          remoteMeta = "../.remotemetafile.json";
          break;
        case "Linux":
          localMeta = "../.metafile.json";
          remoteMeta = "../.remotemetafile.json";
          break;
        case "Other": break;
    }
  }

public static String readFile(String path, Charset encoding)
  throws IOException
{
  byte[] encoded = Files.readAllBytes(Paths.get(path));
  return new String(encoded, encoding);
}
}
