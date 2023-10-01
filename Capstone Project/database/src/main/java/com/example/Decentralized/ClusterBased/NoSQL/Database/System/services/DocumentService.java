package com.example.Decentralized.ClusterBased.NoSQL.Database.System.services;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Collection;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Database;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.DocumentSchema;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Indexing;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.FileManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class DocumentService {
    public void createDocument(String databaseName, String collectionName, String jsonDocument, Optional<String> id) throws IOException {
//        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().writeLock().lock();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonDocument);
            String theId;
            if (id.isPresent()) {
                theId = id.get();
            } else {
                UUID uuid = UUID.randomUUID();
                theId = uuid.toString().substring(0, 8);
            }
            ((ObjectNode) jsonNode).put("id", theId);

            Database database = DatabaseManager.getInstance().getDatabases().get(databaseName);
            Collection collection = database.getCollections().get(collectionName);


            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName)) {
                System.out.println("database doesnt exists");
                return;
            }

            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName + "/" + collectionName)) {
                System.out.println("collection doesnt exists");
                return;
            }

            if (FileManager.fileExists(FileManager.storagePath + "/" + databaseName + "/" + collectionName + "/" + theId + ".json")) {
                System.out.println("document already exists");
            } else {
                if (DocumentSchema.verifyJsonFileWithSchema(jsonNode, FileManager.getDocument(databaseName, collectionName, "schema"))) {
                    objectMapper.writeValue(FileManager.createJsonFile(FileManager.storagePath + "/" + databaseName + "/" + collectionName, theId), jsonNode);
                    collection.getDocuments().add(theId);
                    Indexing.indexDocument(databaseName, collectionName, theId);
                    DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().put(theId, new ReentrantReadWriteLock());
                } else {
                    System.out.println("The document doest follow the schema");
                }
            }
        } finally {
//            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().writeLock().unlock();
        }
    }

    public void deleteDocument(String databaseName, String collectionName, String documentName) {
//        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().writeLock().lock();
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().get(documentName).writeLock().lock();
        boolean deleted = false;
        try {

            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName)) {
                System.out.println("db doesnt exists");
                return;
            }

            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName + "/" + collectionName)) {
                System.out.println("collection doesnt exists");
                return;
            }

            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName + "/" + collectionName + "/" + documentName + ".json")) {
                System.out.println("document doesnt exists");
                return;
            }

            Indexing.removeIndexDocument(databaseName, collectionName, documentName);

            if (FileSystemUtils.deleteRecursively(new File(FileManager.storagePath + "/" + databaseName + "/" + collectionName, documentName + ".json"))) {
                System.out.println("document deleted successfully");
                DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocuments().remove(documentName);
                deleted=true;
            } else {
                System.out.println("failed to delete document");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().get(documentName).writeLock().unlock();
            if(deleted) {
                DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().remove(documentName);
            }
//            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().writeLock().unlock();
        }
    }

    public void updateDocument(String databaseName, String collectionName, String documentName, String jsonDocument) {
//        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().writeLock().lock();
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().get(documentName).writeLock().lock();
        try {
            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName)) {
                System.out.println("db doesnt exists");
                return;
            }

            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName + "/" + collectionName)) {
                System.out.println("collection doesnt exists");
                return;
            }

            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName + "/" + collectionName + "/" + documentName + ".json")) {
                System.out.println("document doesnt exists");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode data = objectMapper.readTree(new File(FileManager.storagePath + "/" + databaseName + "/" + collectionName + "/" + documentName + ".json"));
            JsonNode jsonNode = objectMapper.readTree(jsonDocument);
            Map<String, String> newData = DocumentSchema.getAttributeMap(jsonNode);

            ObjectNode dataNode;
            if (data.isObject()) {
                dataNode = (ObjectNode) data;
            } else {
                dataNode = objectMapper.createObjectNode();
            }

            for (String key : newData.keySet()) {
                dataNode.put(key, newData.get(key));
            }


            if (DocumentSchema.verifyJsonFileWithSchema(dataNode, FileManager.getDocument(databaseName, collectionName, "schema"))) {
                Indexing.removeIndexDocument(databaseName, collectionName, documentName);
                objectMapper.writeValue(FileManager.createJsonFile(FileManager.storagePath + "/" + databaseName + "/" + collectionName, documentName), dataNode);
                Indexing.indexDocument(databaseName, collectionName, documentName);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
//            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().writeLock().unlock();
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().get(documentName).writeLock().lock();
        }
    }


    public List<JsonNode> getAll(String databaseName, String collectionName) throws IOException {
//        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().readLock().lock();
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().forEach((s, reentrantReadWriteLock) -> reentrantReadWriteLock.readLock().lock());
        try {
            Database database = DatabaseManager.getInstance().getDatabases().get(databaseName);
            Collection collection = database.getCollections().get(collectionName);
            List<JsonNode> documents = new ArrayList<>();
            for (String documentId : collection.getDocuments()) {
                documents.add(FileManager.getDocument(databaseName, collectionName, documentId));
            }
            return documents;
        } finally {
//            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().readLock().unlock();
            DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().forEach((s, reentrantReadWriteLock) -> reentrantReadWriteLock.readLock().unlock());
        }
    }


}
