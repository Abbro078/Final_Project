package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Collection;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Database;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
public class ShutdownManager {

    @PreDestroy
    public void destruct() throws IOException {
        loadDatabases(".\\storage");
    }

    public static void loadDatabases(String dir) throws IOException {
        File[] db = Objects.requireNonNull(new File(dir).listFiles());

        for (File file : db) {
            if (file.isDirectory()) {
                String databaseName = file.getName();

                File[] col = Objects.requireNonNull(new File(String.valueOf(file)).listFiles());
                for (File coll : col) {
                    if (coll.isDirectory()) {
                        String collectionName = coll.getName();
                        ObjectMapper objectMapperIndex = new ObjectMapper();
                        ObjectMapper objectMapperAffinity = new ObjectMapper();
                        String jsonIndex = objectMapperIndex.writeValueAsString(DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getIndex());
                        String jsonAffinity = objectMapperAffinity.writeValueAsString(DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getAffinity());
                        JsonNode jsonNodeIndex = objectMapperIndex.readTree(jsonIndex);
                        JsonNode jsonNodeAffinity = objectMapperAffinity.readTree(jsonAffinity);
                        objectMapperIndex.writeValue(FileManager.createJsonFile(FileManager.storagePath+"/"+databaseName+"/"+collectionName, "index"), jsonNodeIndex);
                        objectMapperAffinity.writeValue(FileManager.createJsonFile(FileManager.storagePath+"/"+databaseName+"/"+collectionName, "affinity"), jsonNodeAffinity);

                    }
                }
            }
        }
    }
}
