package utilities.crypt;

import utilities.common.Utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * author: yatao.wang@aliyun.com
 * JDK: 8
 */
public abstract class AlgorithmProvider {
  public static AlgorithmProvider create(String algorithm, String key) {
    if (null == algorithm) {
      return new PlainTextProvider();
    } else if (algorithm.equalsIgnoreCase("SHA1") || algorithm.equalsIgnoreCase("SHA-1")) {
      return new SHA1Provider();
    } else if (algorithm.equalsIgnoreCase("SHA1SHA1")) {
      return new SHA1SHA1Provider();
    } else if (algorithm.equalsIgnoreCase("SHA256") || algorithm.equalsIgnoreCase("SHA-256")) {
      return new SHA256Provider();
    } else if (algorithm.equalsIgnoreCase("BASE64")) {
      return new Base64Provider();
    } else if (algorithm.equalsIgnoreCase("AES")) {
      return new AESProvider(key);
    } else {
      throw new RuntimeException("Not support algorithm '" + algorithm + "'");
    }
  }

  public abstract byte[] encrypt(byte[] input) throws Exception;
  public abstract byte[] decrypt(byte[] input) throws Exception;
}

final class PlainTextProvider extends AlgorithmProvider {
  @Override
  public byte[] encrypt(byte[] input) throws Exception {
    return input;
  }

  @Override
  public byte[] decrypt(byte[] input) throws Exception {
    return input;
  }
}

final class SHA1Provider extends AlgorithmProvider {
  @Override
  public byte[] encrypt(byte[] input) throws Exception {
    if (null == input) {
      return null;
    }

    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    return digest.digest(input);
  }

  @Override
  public byte[] decrypt(byte[] input) throws Exception {
    throw new Exception("Not supported yet");
  }
}

final class SHA1SHA1Provider extends AlgorithmProvider {
  @Override
  public byte[] encrypt(byte[] input) throws Exception {
    if (null == input) {
      return null;
    }

    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    return digest.digest(digest.digest(input));
  }

  @Override
  public byte[] decrypt(byte[] input) throws Exception {
    throw new Exception("Not supported yet");
  }
}

final class SHA256Provider extends AlgorithmProvider {
  @Override
  public byte[] encrypt(byte[] input) throws Exception {
    if (null == input) {
      return null;
    }

    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return digest.digest(input);
  }

  @Override
  public byte[] decrypt(byte[] input) throws Exception {
    throw new Exception("Not supported yet");
  }
}

final class Base64Provider extends AlgorithmProvider {
  @Override
  public byte[] encrypt(byte[] input) throws Exception {
    if (null == input) {
      return null;
    }

    return Base64.getEncoder().encode(input);
  }

  @Override
  public byte[] decrypt(byte[] input) throws Exception {
    if (null == input) {
      return null;
    }

    return Base64.getDecoder().decode(input);
  }
}

final class AESProvider extends AlgorithmProvider {
  private byte[] _keys;
  private byte[] _iv;

  public AESProvider(String key) {
    if (Utilities.getLength(key) != 32) {
      throw new RuntimeException("The key must be 32 characters");
    }

    byte[] bytes = Utilities.getBytes(key);
    int len = bytes.length;
    this._keys = new byte[16];
    this._iv = new byte[16];
    System.arraycopy(bytes, 0, this._keys, 0, this._keys.length);
    System.arraycopy(bytes, 16, this._iv, 0, this._iv.length);
  }

  @Override
  public byte[] encrypt(byte[] input) throws Exception {
    if (null == input) {
      return null;
    }

    Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this._keys, "AES"), new IvParameterSpec(this._iv));
    return cipher.doFinal(input);
  }

  @Override
  public byte[] decrypt(byte[] input) throws Exception {
    if (null == input) {
      return null;
    }

    Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this._keys, "AES"), new IvParameterSpec(this._iv));
    return cipher.doFinal(input);
  }
}