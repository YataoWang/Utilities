package utilities.common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * author: yatao.wang@aliyun.com
 */
public class Utilities {
  public static final String UTF8_CHARSET = "UTF-8";

  public static byte[] getBytes(String input) {
    return getBytes(input, UTF8_CHARSET);
  }

  public static byte[] getBytes(String input, String charset) {
    if (null == input) {
      return null;
    }

    return input.getBytes(Charset.forName(charset));
  }

  public static boolean isNullOrEmpty(String text) {
    return null == text || text.length() <= 0;
  }

  public static int getLength(String text) {
    if (Utilities.isNullOrEmpty(text)) {
      return 0;
    }

    return text.length();
  }

  public static boolean byteArrayEquals(byte[] left, byte[] right) {
    if (null == left | null == right) {
      return false;
    }

    if (left.length != right.length) {
      return false;
    } else {
      for (int i = 0; i < left.length; i++) {
        if (left[i] != right[i]) {
          return false;
        }
      }
    }

    return true;
  }

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

  public static List<String> getClassNameFromJar(String jarPath) {
    List<String> classes = new ArrayList<>();
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(jarPath);
      Enumeration entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        if (!entry.isDirectory() && Utilities.isValidClass(entry.getName())) {
          String className = entry.getName().replace('/', '.');
          className = className.substring(0, className.length() - 6);
          classes.add(className);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (zipFile != null) {
        try {
          zipFile.close();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    return classes;
  }

  public static boolean isValidClass(String clsName) {
    return clsName.endsWith(".class") && clsName.indexOf("$") < 0;
  }
}
