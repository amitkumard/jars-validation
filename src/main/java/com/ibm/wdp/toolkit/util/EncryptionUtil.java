/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2015, 2016
 */
package com.ibm.wdp.toolkit.util;


import javax.xml.bind.DatatypeConverter;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * class for performing encryption and decryption.
 *
 */

public class EncryptionUtil {

  private static final String SALT = "SaltySalt";

  private static final int IV_LENGTH = 16;

  /**
   *
   * Converts a string to a byte array which is used to generate key that is
   * to be used in the Public Key Encryption and Decryption
   *
   * @return
   *          Returns byte array
   * @throws Exception
   *          If an exception occurs while converting the string to byte array
   */

  private static byte[] getSaltBytes() throws Exception {
    return SALT.getBytes("UTF-8");
  }

  /**
   *
   * Converts a string to a character array
   *
   * @return
   * Returns an array of characters
   */

  private static char[] getMasterPassword() {
    return "SuperSecretPassword".toCharArray();
  }

  /**
   *
   * Performs encryption on a string
   *
   * @param input
   * The string on which the encryption is to be performed
   * @return
   * Returns the string in encrypted form as a string
   * @throws Exception
   * If an exception occurs while encryption
   */

  public static String encrpytString(String input) throws Exception {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    PBEKeySpec spec = new PBEKeySpec(getMasterPassword(), getSaltBytes(), 65536, 128);
    SecretKey secretKey = factory.generateSecret(spec);
    SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    byte[] ivBytes = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
    byte[] encryptedTextBytes = cipher.doFinal(input.getBytes("UTF-8"));
    byte[] finalByteArray = new byte[ivBytes.length + encryptedTextBytes.length];
    System.arraycopy(ivBytes, 0, finalByteArray, 0, ivBytes.length);
    System.arraycopy(encryptedTextBytes, 0, finalByteArray, ivBytes.length, encryptedTextBytes.length);
    return DatatypeConverter.printBase64Binary(finalByteArray);
  }

  /**
   *
   * Performs decryption of the string
   *
   * @param input
   * The string on which decryption is to be performed
   * @return
   * Returns the original contents of the string as a string
   * @throws Exception
   * If an exception occurs while decryption
   */

  public static String decrpytString(String input) throws Exception {
    if (input.length() <= IV_LENGTH) {
      throw new Exception("The input string is not long enough to contain the initialisation bytes and data.");
    }
    byte[] byteArray = DatatypeConverter.parseBase64Binary(input);
    byte[] ivBytes = new byte[IV_LENGTH];
    System.arraycopy(byteArray, 0, ivBytes, 0, 16);
    byte[] encryptedTextBytes = new byte[byteArray.length - ivBytes.length];
    System.arraycopy(byteArray, IV_LENGTH, encryptedTextBytes, 0, encryptedTextBytes.length);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    PBEKeySpec spec = new PBEKeySpec(getMasterPassword(), getSaltBytes(), 65536, 128);
    SecretKey secretKey = factory.generateSecret(spec);
    SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
    byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
    return new String(decryptedTextBytes);
  }
}
