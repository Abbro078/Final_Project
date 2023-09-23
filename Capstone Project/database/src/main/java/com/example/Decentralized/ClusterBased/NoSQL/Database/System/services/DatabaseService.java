package com.example.Decentralized.ClusterBased.NoSQL.Database.System.services;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Database;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.ClusterManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.FileManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DatabaseService {

    public void createDatabase(String databaseName) throws IOException {
        DatabaseManager.getInstance().getDatabaseLock().writeLock().lock();
        try{
            if(FileManager.fileExists(FileManager.storagePath +"/"+databaseName)){
                System.out.println("database exists");
                return;
            }
            FileManager.createDirectoryIfNotFound(FileManager.storagePath+"/"+databaseName);
            Database database = new Database();
            database.setName(databaseName);
            DatabaseManager.getInstance().getDatabases().put(databaseName,database);
            DatabaseManager.getInstance().getDatabaseLock().writeLock();
        }finally {
            DatabaseManager.getInstance().getDatabaseLock().writeLock().unlock();
        }
    }

    public void deleteDatabase(String databaseName) {
        DatabaseManager.getInstance().getDatabaseLock().writeLock().lock();
        try{
            if(!FileManager.fileExists(FileManager.storagePath+"/"+databaseName)){
                System.out.println("database doesnt exist");
                return;
            }

            if(FileSystemUtils.deleteRecursively(new File(FileManager.storagePath, databaseName))) {
                System.out.println("database deleted successfully");
                DatabaseManager.getInstance().getDatabases().remove(databaseName);
            } else {
                System.out.println("failed to delete database");
            }
        } finally {
            DatabaseManager.getInstance().getDatabaseLock().writeLock().unlock();
        }
    }

    public List<String> getDatabases(){
        DatabaseManager.getInstance().getDatabaseLock().readLock().lock();
        try{
            return DatabaseManager.getInstance().getDatabases().keySet().stream().toList();
        }finally {
            DatabaseManager.getInstance().getDatabaseLock().readLock().unlock();
        }
    }
}
