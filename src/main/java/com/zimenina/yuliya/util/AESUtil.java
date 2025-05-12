package com.zimenina.yuliya.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES encryption and decryption utility class.
 * Uses a master password as the encryption key.
 */
public class AESUtil {
    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);
    private static final String ALGORITHM = "AES";
    private static String masterPassword;

    /**
     * Sets the master password to be used for encryption/decryption.
     */
    public static void setMasterPassword(String password) {
        masterPassword = password;
    }

    /**
     * Generates a 16-byte key from the provided key string.
     */
    private static byte[] generateKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] aesKey = new byte[16]; // AES-128 requires a 16-byte key
        System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, aesKey.length));
        return aesKey;
    }

    /**
     * Encrypts the given data using the provided key.
     */
    public static String encrypt(String data, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(generateKey(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            String result = Base64.getEncoder().encodeToString(encrypted);
            logger.info("Data encrypted successfully");
            return result;
        } catch (Exception e) {
            logger.error("Encryption error: ", e);
            throw new RuntimeException("Data encryption error", e);
        }
    }

    /**
     * Decrypts the given encrypted data using the provided key.
     */
    public static String decrypt(String encryptedData, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(generateKey(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            String result = new String(decrypted);
            logger.info("Data successfully decrypted");
            return result;
        } catch (Exception e) {
            logger.error("Decryption error: ", e);
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Encrypts data using the master password as the key.
     */
    public static String encrypt(String data) {
        if (masterPassword == null) {
            throw new IllegalStateException("Master password not set");
        }
        return encrypt(data, masterPassword);
    }

    /**
     * Decrypts data using the master password as the key.
     */
    public static String decrypt(String encryptedData) {
        if (masterPassword == null) {
            throw new IllegalStateException("Master password not set");
        }
        return decrypt(encryptedData, masterPassword);
    }
}