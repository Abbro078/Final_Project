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
//        DatabaseManager.getInstance().getDatabaseLock().writeLock().lock();
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
//            DatabaseManager.getInstance().getDatabaseLock().writeLock();
        } finally {
//            DatabaseManager.getInstance().getDatabaseLock().writeLock().unlock();
        }
    }

    public void deleteDatabase(String databaseName) {
        LockManager.writeLockDatabase(databaseName);
        boolean deleted = false;
//        DatabaseManager.getInstance().getDatabaseLock().writeLock().lock();
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
//            DatabaseManager.getInstance().getDatabaseLock().writeLock().unlock();
        }
    }

    public List<String> getDatabases() {
//        DatabaseManager.getInstance().getDatabaseLock().readLock().lock();
//        DatabaseManager.getInstance().getDatabaseLock().forEach((s, reentrantReadWriteLock) -> reentrantReadWriteLock.readLock().lock());
        LockManager.writeLockDatabases();
        try {
            return DatabaseManager.getInstance().getDatabases().keySet().stream().toList();
        } finally {
            LockManager.writeUnlockDatabases();
//            DatabaseManager.getInstance().getDatabaseLock().forEach((s, reentrantReadWriteLock) -> reentrantReadWriteLock.readLock().unlock());
//            DatabaseManager.getInstance().getDatabaseLock().readLock().unlock();
        }
    }
}
