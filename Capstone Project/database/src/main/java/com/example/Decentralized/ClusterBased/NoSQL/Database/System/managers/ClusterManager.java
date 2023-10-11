package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import lombok.Data;

import java.util.List;

@Data
public class ClusterManager {
    private static volatile ClusterManager instance;
    List<String> ports;

    private ClusterManager() {
    }

    public static ClusterManager getInstance() {
        ClusterManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (ClusterManager.class) {
            if (instance == null) {
                instance = new ClusterManager();
            }
            return instance;
        }
    }

    public static int clusterSize() {
        return ClusterManager.getInstance().getPorts().size();
    }

    public static int nextNode() {
        return (ClusterManager.getInstance().getPorts().indexOf(System.getenv("Node_Port")) + 1) % clusterSize();
    }

    public static String get(int port) {
        return ClusterManager.getInstance().getPorts().get(port);
    }

    public static int nodeIndex() {
        return ClusterManager.getInstance().getPorts().indexOf(System.getenv("Node_Port"));
    }

}
