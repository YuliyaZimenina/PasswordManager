package com.zimenina.yuliya.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zimenina.yuliya.model.PasswordEntry;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading password entries to/from a file.
 * Uses JSON for serialization and AES encryption with the master password.
 */
public class Storage {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);
    private static final Gson gson = new Gson();

    /**
     * Saves the given list of password entries to a file.
     * @param entries the list of password entries to save
     * @param fileName the name of the file to save to
     */
    public static void save(ObservableList<PasswordEntry> entries, String fileName) {
        try {
            logger.info("Saving data to a file: {}", fileName);
            List<PasswordEntry> list = new ArrayList<>(entries);
            String json = gson.toJson(list);
            logger.info("The data is serialized in JSON: {}", json);
            String encrypted = AESUtil.encrypt(json);
            logger.info("The data is encrypted");
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(encrypted);
                logger.info("Data successfully written to file");
            }
        } catch (Exception e) {
            logger.error("Error saving data: ", e);
            throw new RuntimeException("Error saving data", e);
        }
    }

    /**
     * Loads the password entries from a file.
     * @param fileName the name of the file to load from
     * @return the list of password entries
     */
    public static List<PasswordEntry> load(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            logger.info("File {} does not exist, returning empty list", fileName);
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) sb.append((char) c);
            logger.info("Data read from file: {}", sb.toString());
            String decrypted = AESUtil.decrypt(sb.toString());
            logger.info("Data decrypted: {}", decrypted);
            Type listType = new TypeToken<List<PasswordEntry>>(){}.getType();
            List<PasswordEntry> result = gson.fromJson(decrypted, listType);
            logger.info("Data deserialized, records:{}", result != null ? result.size() : 0);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error loading data: ", e);
            throw new RuntimeException("Error loading data", e);
        }
    }
}