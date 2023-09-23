package com.example.Decentralized.ClusterBased.NoSQL.Database.System.response;

import lombok.Data;

@Data
public class CollectionResponse {
    String name;
    int size;

    public CollectionResponse(String name, int size) {
        this.name = name;
        this.size = size;
    }
}
