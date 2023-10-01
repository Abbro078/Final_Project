package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Database;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
public class DatabaseManager {
    private static volatile DatabaseManager instance;
    private HashMap<String, Database> databases = new HashMap<>();
//    private ReentrantReadWriteLock databaseLock = new ReentrantReadWriteLock();
    private Map<String, ReentrantReadWriteLock> databaseLock = new HashMap<>();

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        DatabaseManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                instance = new DatabaseManager();
            }
            return instance;
        }
    }

    public static int getAffinityNode(String databaseName, String collectionName) {
        return DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getAffinity();
    }

}
