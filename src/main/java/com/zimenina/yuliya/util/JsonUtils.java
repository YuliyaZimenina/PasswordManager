package com.zimenina.yuliya.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zimenina.yuliya.model.PasswordEntry;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Utility class for saving and loading password entries to and from a JSON file.
 */
public class JsonUtils {
    private static final String FILE_NAME = "passwords.json";

    /**
     * Saves a list of PasswordEntry objects to a JSON file.
     *
     * @param entries the list of PasswordEntry objects to save
     */
    public static void saveToJson(List<PasswordEntry> entries) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            Gson gson = new Gson();
            gson.toJson(entries, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a list of PasswordEntry objects from a JSON file.
     *
     * @return the list of PasswordEntry objects, or null if an error occurs
     */
    public static List<PasswordEntry> loadFromJson() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PasswordEntry>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}