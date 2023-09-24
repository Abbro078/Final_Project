package com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.FileManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Indexing {
    public static void newIndex(String databaseName, String collectionName, String key) throws IOException {
        Database database = DatabaseManager.getInstance().getDatabases().get(databaseName);
        Collection collection = database.getCollections().get(collectionName);
        File schema = new File(FileManager.storagePath + "/" + databaseName + "/" + collectionName + "/" + "schema.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(schema);
        if (DocumentSchema.getAttributeMap(jsonNode).containsKey(key)) {
            collection.getIndex().put(key, new HashMap<>());

            for (String documentId : collection.getDocuments()) {
                Map<String, String> att = DocumentSchema.getAttributeMap(FileManager.getDocument(databaseName, collectionName, documentId));
                if (att.containsKey(key)) {
                    if (!collection.getIndex().get(key).containsKey(att.get(key))) {
                        IndexObject indexObject = new IndexObject();
                        Map<String, IndexObject> indexObjectMap = new HashMap<>();
                        indexObject.getIndexes().add(att.get("id"));
                        indexObjectMap.put(att.get(key), indexObject);
                        collection.getIndex().get(key).put(att.get(key), indexObjectMap.get(att.get(key)));
                    } else {
                        IndexObject existingIndexObject = collection.getIndex().get(key).get(att.get(key));
                        existingIndexObject.getIndexes().add(documentId);
                    }
                }
            }
        } else {
            throw new IOException("the key you want to be indexed is incorrect");
        }
    }

    public static void indexDocument(String databaseName, String collectionName, String documentName) throws IOException {
        Database database = DatabaseManager.getInstance().getDatabases().get(databaseName);
        Collection collection = database.getCollections().get(collectionName);
        Map<String, String> att = DocumentSchema.getAttributeMap(FileManager.getDocument(databaseName, collectionName, documentName));
        for (String key : collection.getIndex().keySet()) {
            if (att.containsKey(key)) {
                if (!collection.getIndex().get(key).containsKey(att.get(key))) {
                    IndexObject indexObject = new IndexObject();
                    Map<String, IndexObject> indexObjectMap = new HashMap<>();
                    indexObject.getIndexes().add(att.get("id"));
                    indexObjectMap.put(att.get(key), indexObject);
                    collection.getIndex().get(key).put(att.get(key), indexObjectMap.get(att.get(key)));
                } else {
                    IndexObject existingIndexObject = collection.getIndex().get(key).get(att.get(key));
                    existingIndexObject.getIndexes().add(documentName);
                }
            }
        }
    }

    public static void removeIndexDocument(String databaseName, String collectionName, String documentName) throws IOException {
        Map<String, String> att = DocumentSchema.getAttributeMap(FileManager.getDocument(databaseName, collectionName, documentName));
        for (String key : DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getIndex().keySet()) {
            IndexObject existingIndexObject = DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getIndex().get(key).get(att.get(key));
            existingIndexObject.getIndexes().remove(documentName);

            if (existingIndexObject.getIndexes().isEmpty()) {
                DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getIndex().keySet().remove(key);
            }
        }
    }
}
