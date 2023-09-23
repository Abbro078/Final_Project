package com.example.Decentralized.ClusterBased.NoSQL.Database.System.services;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.*;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Collection;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.AffinityManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.ClusterManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.FileManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.response.CollectionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.DocumentSchema.verifyJsonTypes;

@Service
public class CollectionService {

    public void createCollection(String databaseName, String collectionName, String schema) throws IOException {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().writeLock().lock();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(schema);

            if (!FileManager.fileExists(FileManager.storagePath+"/"+databaseName)) {
                System.out.println("database doesnt exists");
                return;
            }

            if (FileManager.fileExists(FileManager.storagePath+"/"+databaseName+"/"+collectionName)) {
                System.out.println("collection already exists");
            } else {
                if (verifyJsonTypes(jsonNode)) {
                    FileManager.createDirectoryIfNotFound(FileManager.storagePath+"/"+databaseName+"/"+collectionName);
                    Collection collection = new Collection();
                    collection.setName(collectionName);
                    DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().put(collectionName, collection);
                    DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).setAffinity(AffinityManager.getNextAffinity());
                    System.out.println("collection created successfully");
                    objectMapper.writeValue(FileManager.createJsonFile(FileManager.storagePath+"/"+databaseName+"/"+collectionName, "schema"), jsonNode);
                } else {
                    System.out.println("Failed to create collection");
                }
            }
        } finally {
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().writeLock().unlock();
        }
    }

    public void deleteCollection(String databaseName, String collectionName) {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().writeLock().lock();
        try {
            if (!FileManager.fileExists(FileManager.storagePath+"/"+databaseName)) {
                System.out.println("database doesnt exists");
                return;
            }

            if (!FileManager.fileExists(FileManager.storagePath+"/"+databaseName+"/"+collectionName)) {
                System.out.println("collection doesnt exists");
                return;
            }

            if(FileSystemUtils.deleteRecursively(new File(FileManager.storagePath+"/"+databaseName, collectionName))) {
                System.out.println("collection deleted successfully");
                DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().remove(collectionName);
            } else {
                System.out.println("failed to delete collection");
            }
        } finally {
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().writeLock().unlock();
        }
    }

    public List<CollectionResponse> getCollections(String databaseName) {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().readLock().lock();
        try{
            List<CollectionResponse > collections=new ArrayList<>();
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().forEach((s, collection) -> {
                collections.add(new CollectionResponse(s,collection.getDocuments().size()));
            });
            return collections;
        }finally {
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().readLock().unlock();
        }
    }

    public JsonNode getSchema(String databaseName,String collectionName) throws IOException {
        return FileManager.getDocument(databaseName,collectionName,"schema");
    }

    public void newIndex(String databaseName, String collectionName, String key) throws IOException {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().writeLock().lock();
        try {
            Indexing.newIndex(databaseName, collectionName, key);
        }
        finally {
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().writeLock().unlock();
        }
    }


    public List<JsonNode> filterByKey(String databaseName, String collectionName, String key) throws IOException {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().readLock().lock();
        try {
            List<JsonNode> documents = new ArrayList<>();
            Database database = DatabaseManager.getInstance().getDatabases().get(databaseName);
            Collection collection = database.getCollections().get(collectionName);
            for (String id: collection.getIndex().get(key).keySet()) {
                for (String idx : collection.getIndex().get(key).get(id).getIndexes()){
                    documents.add(FileManager.getDocument(databaseName,collectionName, idx));
                }
            }
            return documents;
        }
        finally {
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().readLock().unlock();
        }
    }

    public List<JsonNode> filterByValue(String databaseName, String collectionName, String key, String value) throws IOException {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().readLock().lock();
        try {
            List<JsonNode> documents = new ArrayList<>();
            Database database = DatabaseManager.getInstance().getDatabases().get(databaseName);
            Collection collection = database.getCollections().get(collectionName);
            for (String id: collection.getIndex().get(key).keySet()) {
                if(Objects.equals(id, value)) {
                    for (String idx : collection.getIndex().get(key).get(id).getIndexes()) {
                        documents.add(FileManager.getDocument(databaseName,collectionName, idx));
                    }
                    break;
                }
            }
            return documents;
        }
        finally {
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().readLock().unlock();
        }
    }
}
