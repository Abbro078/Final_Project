package com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
public class Collection {

    private String name;
    private List<String> documents = new ArrayList<>();
    private List<String> keyIndex = new ArrayList<>();
    private ReadWriteLock documentLock = new ReentrantReadWriteLock();
    private int affinity;

    private Map<String, Map<String, IndexObject>> index = new HashMap<>();

}
