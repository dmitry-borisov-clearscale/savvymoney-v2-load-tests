package com.sm.lt.infrastructure.services.credit_session_manager.enc;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESEncryptionUtils {

    private AESEncryptionUtils() {
        super();
    }

    /**
     * gets the AES encryption key. In your actual programs, this should be safely
     * stored.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey getSecretEncryptionKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(keySize); // The AES key size in number of bits
        return generator.generateKey();
    }

    /**
     * gets the AES encryption key.
     */

    public static String getSecretEncryptionKeyHex(int keySize) {
        try {
            return bytesToHex(getSecretEncryptionKey(keySize).getEncoded());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static SecretKey getSecretEncryptionKeyFromHex(String base64String) throws DecoderException {

        byte[] encoded = hexToBytes(base64String);
        return new SecretKeySpec(encoded, "AES");

    }

    /**
     * Encrypts plainText in AES using the secret key
     *
     * @param plainText
     * @param secKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] encryptText(String plainText, SecretKey secKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        return aesCipher.doFinal(plainText.getBytes());
    }

    public static String encryptTextToHex(String plainText, String secKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, DecoderException {
        return bytesToHex(encryptText(plainText, getSecretEncryptionKeyFromHex(secKey)));
    }

    public static String encryptTextToHex(String plainText, SecretKey secKey) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        return bytesToHex(encryptText(plainText, secKey));
    }

    /**
     * Decrypts encrypted byte array using the key used for encryption.
     *
     * @param byteCipherText
     * @param secKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static String decryptText(byte[] byteCipherText, SecretKey secKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        return new String(bytePlainText);
    }

    public static String decryptHexText(String cipherText, SecretKey secKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, DecoderException {
        return decryptText(hexToBytes(cipherText), secKey);
    }

    public static String decryptHexText(String cipherText, String secKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, DecoderException {
        return decryptText(hexToBytes(cipherText), getSecretEncryptionKeyFromHex(secKey));
    }

    /**
     * Convert a binary byte array into readable Hex form
     */
    private static String bytesToHex(byte[] data) {
        if (data == null)
            return null;

        return Hex.encodeHexString(data);
    }

    private static byte[] hexToBytes(String code) throws DecoderException {
        if (code == null)
            return null;

        return Hex.decodeHex(code.toCharArray());
    }

}