package com.zimenina.yuliya.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES encryption and decryption utility class.
 * This class provides methods to encrypt and decrypt data using AES algorithm.
 */
public class AESUtil {
    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);
    private static final String ALGORITHM = "AES";
    private static final String KEY = "MySecretKey12345"; // 16 characters = 16 bytes

    /**
     * Encrypts the given data using AES algorithm.
     *
     * @param data the data to encrypt
     * @return the encrypted data as a Base64 encoded string
     */
    public static String encrypt(String data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
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
     * Decrypts the given encrypted data using AES algorithm.
     *
     * @param encryptedData the encrypted data to decrypt
     * @return the decrypted data as a string
     */
    public static String decrypt(String encryptedData) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
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
}