package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

public class LockManager {
    public static void writeLockDatabases() {
        DatabaseManager.getInstance().getDatabasesLock().writeLock().lock();
    }

    public static void writeUnlockDatabases() {
        DatabaseManager.getInstance().getDatabasesLock().writeLock().unlock();
    }
    public static void readLockDatabases() {
        DatabaseManager.getInstance().getDatabasesLock().readLock().unlock();
    }
    public static void readUnlockDatabases() {
        DatabaseManager.getInstance().getDatabasesLock().readLock().unlock();
    }

    public static void writeLockDatabase(String databaseName) {
        DatabaseManager.getInstance().getDatabaseLock().get(databaseName).writeLock().lock();
    }

    public static void writeUnlockDatabase(String databaseName){
        DatabaseManager.getInstance().getDatabaseLock().get(databaseName).writeLock().unlock();
    }

    public static void readLockDatabase(String databaseName) {
        DatabaseManager.getInstance().getDatabaseLock().get(databaseName).readLock().lock();
    }

    public static void readUnlockDatabase(String databaseName) {
        DatabaseManager.getInstance().getDatabaseLock().get(databaseName).readLock().unlock();
    }

    public static void writeLockCollection(String databaseName, String collectionName){
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().get(collectionName).writeLock().lock();
    }

    public static void writeUnlockCollection(String databaseName, String collectionName){
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().get(collectionName).writeLock().unlock();
    }

    public static void readLockCollection(String databaseName, String collectionName) {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().get(collectionName).writeLock().lock();
    }

    public static void readUnlockCollection(String databaseName, String collectionName) {
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollectionLock().get(collectionName).writeLock().unlock();
    }

    public static void writeLockDocument(String databaseName, String collectionName, String documentName){
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().get(documentName).writeLock().lock();
    }

    public static void writeUnlockDocument(String databaseName, String collectionName, String documentName){
        DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocumentLock().get(documentName).writeLock().unlock();
    }
}
