package com.example.Decentralized.ClusterBased.NoSQL.Database.System.services;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Database;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.FileManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.LockManager;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class DatabaseService {

    public void createDatabase(String databaseName) throws IOException {
        try {
            if (FileManager.fileExists(FileManager.storagePath + "/" + databaseName)) {
                System.out.println("database exists");
                return;
            }
            FileManager.createDirectoryIfNotFound(FileManager.storagePath + "/" + databaseName);
            Database database = new Database();
            database.setName(databaseName);
            DatabaseManager.getInstance().getDatabases().put(databaseName, database);
            DatabaseManager.getInstance().getDatabaseLock().put(databaseName, new ReentrantReadWriteLock());
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void deleteDatabase(String databaseName) {
        LockManager.writeLockDatabase(databaseName);
        boolean deleted = false;
        try {
            if (!FileManager.fileExists(FileManager.storagePath + "/" + databaseName)) {
                System.out.println("database doesnt exist");
                return;
            }

            if (FileSystemUtils.deleteRecursively(new File(FileManager.storagePath, databaseName))) {
                System.out.println("database deleted successfully");
                DatabaseManager.getInstance().getDatabases().remove(databaseName);
                deleted = true;
            } else {
                System.out.println("failed to delete database");
            }
        } finally {
            LockManager.writeUnlockDatabase(databaseName);
            if(deleted) {
                DatabaseManager.getInstance().getDatabaseLock().remove(databaseName);
            }
        }
    }

    public List<String> getDatabases() {
        LockManager.readLockDatabases();
        try {
            return DatabaseManager.getInstance().getDatabases().keySet().stream().toList();
        } finally {
            LockManager.readUnlockDatabases();
        }
    }
}
