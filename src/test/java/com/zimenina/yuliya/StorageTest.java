package com.zimenina.yuliya;

import com.google.gson.Gson;
import com.zimenina.yuliya.model.PasswordEntry;
import com.zimenina.yuliya.util.AESUtil;
import com.zimenina.yuliya.util.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Storage class.
 * This class tests the save and load methods of the Storage class.
 */

public class StorageTest {
    private static final String TEST_FILE_NAME = "test_data.json";
    private ObservableList<PasswordEntry> entries;
    private MockedStatic<AESUtil> aesUtilMock;
    private File tempFile;

    @BeforeEach
    void setUp() {
        // Initialize mock for AESUtil
        aesUtilMock = mockStatic(AESUtil.class);

        // Create a temporary file
        tempFile = new File(TEST_FILE_NAME);
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // Create test data
        entries = FXCollections.observableArrayList();
        entries.add(new PasswordEntry("Service1", "User1", "Pass1"));
        entries.add(new PasswordEntry("Service2", "User2", "Pass2"));
    }

    @AfterEach
    void tearDown() {
        // Delete the temporary file after the test
        if (tempFile.exists()) {
            tempFile.delete();
        }
        // Close the mock if it was initialized
        if (aesUtilMock != null) {
            aesUtilMock.close();
        }
    }

    @Test
    void testSaveAndLoad() throws Exception {
        // Set up mock for encryption: return JSON string unchanged
        Gson gson = new Gson();
        String json = gson.toJson(entries);
        when(AESUtil.encrypt(json)).thenReturn(json);
        when(AESUtil.decrypt(json)).thenReturn(json);

        // Save the data
        Storage.save(entries, TEST_FILE_NAME);

        // Verify that the file was created
        assertTrue(tempFile.exists());

        // Read file contents manually to verify
        try (FileReader reader = new FileReader(tempFile)) {
            char[] buffer = new char[(int) tempFile.length()];
            reader.read(buffer);
            String fileContent = new String(buffer);
            assertEquals(json, fileContent);
        }

        // Load the data
        java.util.List<PasswordEntry> loadedEntries = Storage.load(TEST_FILE_NAME);

        // Verify that the data was loaded correctly
        assertEquals(entries.size(), loadedEntries.size());
        assertEquals(entries.get(0).getService(), loadedEntries.get(0).getService());
        assertEquals(entries.get(0).getUsername(), loadedEntries.get(0).getUsername());
        assertEquals(entries.get(0).getPassword(), loadedEntries.get(0).getPassword());
    }

    @Test
    void testLoadWhenFileDoesNotExist() {
        // Ensure the file does not exist
        assertFalse(tempFile.exists());

        // Load the data
        java.util.List<PasswordEntry> loadedEntries = Storage.load(TEST_FILE_NAME);

        // Verify that an empty list is returned
        assertNotNull(loadedEntries);
        assertTrue(loadedEntries.isEmpty());
    }

    @Test
    void testSaveThrowsExceptionOnFailure() throws Exception {
        // Set up mock for encryption
        String json = new Gson().toJson(entries);
        when(AESUtil.encrypt(json)).thenReturn(json);

        // Make the file read-only to trigger a write error
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("test");
        }
        tempFile.setReadOnly();

        // Verify that saving throws an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Storage.save(entries, TEST_FILE_NAME);
        });
        assertTrue(exception.getMessage().contains("Error saving data"));
    }
}