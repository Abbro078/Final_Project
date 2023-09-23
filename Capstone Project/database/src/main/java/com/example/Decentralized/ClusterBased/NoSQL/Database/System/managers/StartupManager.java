package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Collection;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Database;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.IndexObject;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.services.BootstrappingService;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.util.JacksonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.FileManager.readNumberFromFile;


@Component
@RequiredArgsConstructor
public class StartupManager {

    private static final BootstrappingService bootstrappingService = new BootstrappingService();

    @PostConstruct
    public static void init() throws IOException, InterruptedException {
        DatabaseManager.getInstance().getDatabases().clear();
        loadDatabases("storage");
        Thread.sleep(2000);
    }


    public static void loadDatabases(String dir) throws IOException {
        if(!(new File(dir)).exists())return;
        File[] db = Objects.requireNonNull(new File(dir).listFiles());
        for (File file : db) {
            if (file.isDirectory()) {
                processDatabase(file);
            }
        }
    }

    private static void processDatabase(File file) throws IOException {
        String databaseName = file.getName();
        int databaseIndex = databaseName.lastIndexOf("\\");
        Database database = new Database();
        database.setName(databaseName);
        DatabaseManager.getInstance().getDatabases().put(databaseName,database);

        File[] col = Objects.requireNonNull(new File(String.valueOf(file)).listFiles());
        for (File coll : col) {
            if (coll.isDirectory()) {
                processCollection(coll, databaseName);
            }
        }
    }

    private static void processCollection(File coll, String databaseName) throws IOException {
        String collectionName = coll.getName();
        int collectionIndex = collectionName.lastIndexOf("\\");
        Collection collection = new Collection();
        collection.setName(collectionName.substring(collectionIndex + 1));
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().put(collectionName.substring(collectionIndex + 1), collection);

        loadDocuments(coll, databaseName, collectionName);

        loadIndex(databaseName, collectionName);
    }

    private static void loadIndex(String databaseName, String collectionName) throws IOException {
        JsonNode index=FileManager.getDocument(databaseName, collectionName,"index");
        for(String indexAttribute: JacksonUtils.getKeys(index)){
            JsonNode indexKeyMap=index.get(indexAttribute);
            for(String indexValue:JacksonUtils.getKeys(indexKeyMap)){
                JsonNode indexValueMap=indexKeyMap.get(indexValue);
                ObjectMapper mapper = new ObjectMapper();
                List<String> ids=mapper.convertValue(indexValueMap.get("indexes"), ArrayList.class);
                var theIndex=DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getIndex();
                if(!theIndex.containsKey(indexAttribute)){
                    theIndex.put(indexAttribute,new HashMap<>());
                }
                IndexObject indexObject=new IndexObject();
                indexObject.setIndexes(ids);
                theIndex.get(indexAttribute).put(indexValue,indexObject);
            }
        }
    }

    private static void loadDocuments(File coll, String databaseName, String collectionName) throws IOException {
        File[] doc = Objects.requireNonNull(new File(String.valueOf(coll)).listFiles());
        for (File docu : doc) {
            if (!docu.isDirectory()) {
                String documentName = docu.getName();
                int documentIndex = documentName.lastIndexOf("\\");
                int documentIndex2 = documentName.lastIndexOf(".");
                if(documentName.substring(documentIndex + 1, documentIndex2).equals("affinity")) {
                    int number = readNumberFromFile(FileManager.storagePath+"/"+databaseName+"/"+collectionName+"/"+"affinity.json");
                    DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).setAffinity(number);
                }
                else if(!documentName.substring(documentIndex + 1, documentIndex2).equals("schema") && !documentName.substring(documentIndex + 1, documentIndex2).equals("index")) {
                    DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocuments().add(documentName.substring(documentIndex + 1,documentIndex2));
                }
            }
        }
    }

}
