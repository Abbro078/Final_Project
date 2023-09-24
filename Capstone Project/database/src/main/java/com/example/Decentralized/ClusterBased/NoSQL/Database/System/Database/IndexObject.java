package com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IndexObject {
    List<String> indexes = new ArrayList<>();
}
