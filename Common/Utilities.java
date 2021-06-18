package utilities.common;

import java.nio.charset.Charset;

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
}
