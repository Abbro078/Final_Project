package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class AffinityManager {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static int getNextAffinity() {
        return counter.getAndIncrement() % ClusterManager.getInstance().getPorts().size();
    }
}