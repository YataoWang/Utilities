package com.wyt.downloader;

import java.io.File;
import java.io.IOException;

class Utils {
  public static boolean isExist(String file) {
    return new File(file).exists();
  }

  public static void ensureExist(String file) throws IOException {
    ensureExist(new File(file));
  }

  public static void ensureExist(File file) throws IOException {
    if (null == file) {
      return;
    }

    if (!file.exists()) {
      file.createNewFile();
    }
  }

  public static void ensureFilePath(String file) {
    File f = new File(file);
    if (!f.exists()) {
      ensureDirectory(f.getParent());
    }
  }

  public static void ensureDirectory(String path) {
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  public static String getErrorMessage(Throwable ex) {
    if (null == ex) {
      return "";
    }

    String message = ex.getMessage();
    if (message != null) {
      return message;
    } else {
      return getFullStack(ex);
    }
  }

  public static String getFullStack(Throwable ex) {
    if (null == ex) {
      return "";
    }

    StringBuilder builder = new StringBuilder();
    StackTraceElement[] elements = ex.getStackTrace();
    for (StackTraceElement element : elements) {
      builder.append("at\t").append(element.toString()).append("\r\n");
    }

    return builder.toString();
  }
}
