package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import org.springframework.web.util.UriComponentsBuilder;

public class RequestManager {

    public static String buildUri(String node, String url, String databaseName, String collectionName, String documentName, String key, Boolean propagate, Boolean fromAffinityNode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://host.docker.internal:" + node + url).queryParam("databaseName", databaseName);
        if (collectionName != null) {
            builder.queryParam("collectionName", collectionName);
        }
        if (documentName != null) {
            builder.queryParam("documentName", documentName);
        }
        if (key != null) {
            builder.queryParam("key", key);
        }
        if (propagate != null) {
            builder.queryParam("propagate", propagate);
        }
        if (fromAffinityNode != null) {
            builder.queryParam("fromAffinityNode", fromAffinityNode);
        }
        return builder.toUriString();
    }
}
