package com.zimenina.yuliya;

import com.zimenina.yuliya.model.PasswordEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PasswordEntry class.
 */
public class PasswordEntryTest {
    private PasswordEntry entry;

    // Initializes a PasswordEntry object before each test with default values
    @BeforeEach
    void setUp() {
        entry = new PasswordEntry("TestService", "TestUser", "TestPassword");
    }

    // Tests the getService method to ensure it returns the correct service name
    @Test
    void testGetService() {
        assertEquals("TestService", entry.getService());
    }

    // Tests the setService method by updating the service name and verifying the change
    @Test
    void testSetService() {
        entry.setService("NewService");
        assertEquals("NewService", entry.getService());
    }

    // Tests the getUsername method to ensure it returns the correct username
    @Test
    void testGetUsername() {
        assertEquals("TestUser", entry.getUsername());
    }

    // Tests the setUsername method by updating the username and verifying the change
    @Test
    void testSetUsername() {
        entry.setUsername("NewUser");
        assertEquals("NewUser", entry.getUsername());
    }

    // Tests the getPassword method to ensure it returns the correct password
    @Test
    void testGetPassword() {
        assertEquals("TestPassword", entry.getPassword());
    }

    // Tests the setPassword method by updating the password and verifying the change
    @Test
    void testSetPassword() {
        entry.setPassword("NewPassword");
        assertEquals("NewPassword", entry.getPassword());
    }

    // Tests getDisplayedPassword when the password is hidden, expecting a masked output
    @Test
    void testGetDisplayedPasswordWhenHidden() {
        entry.setPasswordVisible(false);
        assertEquals("******", entry.getDisplayedPassword());
    }

    // Tests getDisplayedPassword when the password is visible, expecting the actual password
    @Test
    void testGetDisplayedPasswordWhenVisible() {
        entry.setPasswordVisible(true);
        assertEquals("TestPassword", entry.getDisplayedPassword());
    }

    // Tests setPasswordVisible by toggling visibility and verifying the state
    @Test
    void testSetPasswordVisible() {
        entry.setPasswordVisible(true);
        assertTrue(entry.isPasswordVisible());
        entry.setPasswordVisible(false);
        assertFalse(entry.isPasswordVisible());
    }
}