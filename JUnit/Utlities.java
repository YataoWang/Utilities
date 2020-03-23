import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utilities {
  public static boolean isNullOrEmpty(String text) {
    return text == null || text.length() <= 0;
  }

  public static String getCallStack(Throwable ex) {
    StringBuffer sb = new StringBuffer(ex.getClass().getName());
    StackTraceElement[] elements = ex.getStackTrace();
    for (int i = 0; i < elements.length; i++) {
      sb.append("\tat ").append(elements[i].toString());
    }

    return sb.toString();
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
