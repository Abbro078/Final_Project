package com.example.Decentralized.ClusterBased.NoSQL.Database.System.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JacksonUtils {
    public static List<String> getKeys(JsonNode jsonNode){
        List<String> keys = new ArrayList<>();
        Iterator<String> iterator = jsonNode.fieldNames();
        iterator.forEachRemaining(keys::add);
        return keys;
    }
}
