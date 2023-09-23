package com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class IndexObject {
    List<String> indexes= new ArrayList<>();
}
