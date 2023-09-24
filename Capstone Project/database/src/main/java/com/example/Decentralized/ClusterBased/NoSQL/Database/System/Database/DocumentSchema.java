package com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DocumentSchema {
    public static boolean verifyJsonTypes(JsonNode docSchema) {
        Map<String, String> attributeMap = getAttributeMap(docSchema);
        for (String att : attributeMap.values()) {
            boolean exist = false;
            try {
                SchemaTypes schemaTypes = SchemaTypes.valueOf(att);
                for (SchemaTypes type : SchemaTypes.values()) {
                    if (schemaTypes == type) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    return false;
                }
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        boolean hasId = attributeMap.containsKey("id");
        if (!hasId) {
            ((ObjectNode) docSchema).put("id", "schema");
        }
        return true;
    }

    public static Map<String, String> getAttributeMap(JsonNode jsonDoc) {
        Map<String, String> attributeMap = new HashMap<>();

        for (Iterator<String> it = jsonDoc.fieldNames(); it.hasNext(); ) {
            String property = it.next();
            var isObject = jsonDoc.get(property).isObject();
            if (isObject) {
                Map<String, String> childAttributeMap = getAttributeMap(jsonDoc.get(property));
                for (String key : childAttributeMap.keySet()) {
                    attributeMap.put(property + "." + key, childAttributeMap.get(key));
                }
            } else {
                String value = jsonDoc.get(property).asText();
                attributeMap.put(property, value);
            }
        }
        return attributeMap;
    }


    public static boolean verifyJsonFileWithSchema(JsonNode jsonDoc, JsonNode jsonSchema) {
        Map<String, String> jsonSchemaMapping = getAttributeMap(jsonSchema);
        Map<String, String> jsonDocMapping = getAttributeMap(jsonDoc);
        for (String key : jsonDocMapping.keySet()) {
            if (!jsonSchemaMapping.containsKey(key)) return false;
        }
        return true;
    }
}

