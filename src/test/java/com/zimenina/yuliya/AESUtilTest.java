package com.zimenina.yuliya;

import com.zimenina.yuliya.util.AESUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AESUtil class.
 * This class tests the encryption and decryption methods of the AESUtil class.
 */

public class AESUtilTest {
    private static final String TEST_MASTER_PASSWORD = "TestMasterPassword";
    private static final String TEST_DATA = "Hello, World!";

    @BeforeEach
    void setUp() {
        // Set a master password before each test
        AESUtil.setMasterPassword(TEST_MASTER_PASSWORD);
    }

    @Test
    void testEncryptAndDecrypt() {
        // Encrypting data
        String encryptedData = AESUtil.encrypt(TEST_DATA);

        // Check that the encrypted data is not equal to the original
        assertNotEquals(TEST_DATA, encryptedData);

        // Decrypting the data
        String decryptedData = AESUtil.decrypt(encryptedData);

        // We check that after decryption the data matches the original
        assertEquals(TEST_DATA, decryptedData);
    }

    @Test
    void testEncryptWithKeyAndDecryptWithKey() {
        // Encrypting data using the explicit key method
        String encryptedData = AESUtil.encrypt(TEST_DATA, TEST_MASTER_PASSWORD);

        // Check that the encrypted data is not equal to the original
        assertNotEquals(TEST_DATA, encryptedData);

        // Decrypting data using the explicit key method
        String decryptedData = AESUtil.decrypt(encryptedData, TEST_MASTER_PASSWORD);

        // We check that after decryption the data matches the original
        assertEquals(TEST_DATA, decryptedData);
    }

    @Test
    void testEncryptWithoutMasterPassword() {
        // Resetting the master password
        AESUtil.setMasterPassword(null);

        // Check that calling encrypt without a master password throws an exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            AESUtil.encrypt(TEST_DATA);
        });

        assertEquals("Master password not set", exception.getMessage());
    }

    @Test
    void testDecryptWithoutMasterPassword() {
        // Resetting the master password
        AESUtil.setMasterPassword(null);

        // Check that calling decrypt without a master password throws an exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            AESUtil.decrypt("SomeEncryptedData");
        });

        assertEquals("Master password not set", exception.getMessage());
    }
}