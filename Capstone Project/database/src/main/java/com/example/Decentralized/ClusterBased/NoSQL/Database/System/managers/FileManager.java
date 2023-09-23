package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    public final static String storagePath="storage";

    public static boolean fileExists(String databasePath) {
        return directoryOrFileExists(databasePath);
    }

    public static void createDirectoryIfNotFound(String directory) throws IOException {
        Files.createDirectories(Paths.get(directory));
    }
    private static boolean directoryOrFileExists(String pathForFileOrDirectory) {
        Path path = Paths.get(pathForFileOrDirectory);
        return Files.exists(path);
    }

    public static File createJsonFile(String directory, String jsonName)
    {
        return new File(directory, jsonName + ".json");
    }

    public static JsonNode getDocument(String databaseName,String collectionName,String documentId) throws IOException {
        String documentPath=storagePath+"/"+databaseName+"/"+collectionName+"/"+documentId+".json";
        ObjectMapper mapper = new ObjectMapper();
        File document = new File(documentPath);
        JsonNode documentJson = mapper.readTree(document);
        return documentJson;
    }

    public static int readNumberFromFile(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return Integer.parseInt(content.trim());
    }

}
