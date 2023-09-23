package com.example.Decentralized.ClusterBased.NoSQL.Database.System.requests;

import lombok.Data;

import java.util.List;

@Data
public class InitData{
    private List<String> ports;

    @Override
    public String toString() {
        return "InitData{" +
                "ports=" + ports +
                '}';
    }
}