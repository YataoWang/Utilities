package utilities.crypt.test;

import utilities.common.Utilities;
import utilities.crypt.AlgorithmProvider;

public class Test {
  public static void main(String[] args) throws Exception {
    String value = "yatao.wang@aliyun.com";
    String key = "12345678901234567890123456789012";
    AlgorithmProvider provider = AlgorithmProvider.create("SHA1", null);
    byte[] bytes = provider.encrypt(Utilities.getBytes(value));
    String result = new String(AlgorithmProvider.create("BASE64", null).encrypt(bytes));
    System.out.println("SHA1 + BASE64 = " + result);

    provider = AlgorithmProvider.create("SHA1SHA1", null);
    bytes = provider.encrypt(Utilities.getBytes(value));
    result = new String(AlgorithmProvider.create("BASE64", null).encrypt(bytes));
    System.out.println("SHA1SHA1 + BASE64 = " + result);

    provider = AlgorithmProvider.create("SHA256", null);
    bytes = provider.encrypt(Utilities.getBytes(value));
    result = new String(AlgorithmProvider.create("BASE64", null).encrypt(bytes));
    System.out.println("SHA256 + BASE64 = " + result);

    provider = AlgorithmProvider.create("AES", key);
    bytes = provider.encrypt(Utilities.getBytes(value));
    result = new String(AlgorithmProvider.create("BASE64", null).encrypt(bytes));
    System.out.println("AES_Encrypt + BASE64 = " + result);

    bytes = AlgorithmProvider.create("BASE64", null).decrypt(Utilities.getBytes(result));
    provider = AlgorithmProvider.create("AES", key);
    bytes = provider.decrypt(bytes);
    result = new String(bytes);
    System.out.println("BASE64 + AES_Decrypt = " + result);

    provider = AlgorithmProvider.create("AES", key);
    bytes = provider.encrypt(Utilities.getBytes(value));
    bytes = AlgorithmProvider.create("SHA1SHA1", key).encrypt(bytes);
    result = new String(AlgorithmProvider.create("BASE64", null).encrypt(bytes));
    System.out.println("AES_Encrypt + SHA1SHA1 + BASE64 = " + result);

    provider = AlgorithmProvider.create("SHA256", key);
    bytes = provider.encrypt(Utilities.getBytes(value));
    bytes = AlgorithmProvider.create("AES", key).encrypt(bytes);
    result = new String(AlgorithmProvider.create("BASE64", null).encrypt(bytes));
    System.out.println("SHA256 + AES_Encrypt + BASE64 = " + result);

    provider = AlgorithmProvider.create("BASE64", key);
    bytes = provider.decrypt(Utilities.getBytes(result));
    bytes = AlgorithmProvider.create("AES", key).decrypt(bytes);
    result = new String(AlgorithmProvider.create("BASE64", null).encrypt(bytes));
    System.out.println("BASE64 + AES_Decrypt + BASE64(SHA256) = " + result);
  }
}
