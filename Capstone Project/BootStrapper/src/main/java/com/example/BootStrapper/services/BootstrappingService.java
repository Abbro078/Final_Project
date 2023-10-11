package com.example.BootStrapper.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class BootstrappingService {
    private final Map<String, String> users;

    public BootstrappingService() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        users = objectMapper.readValue(new File("users" + "/users.json"), Map.class);
    }

    public boolean login(String username, String password) {
        System.out.println(users);
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public String register(String username, String password) throws IOException {
        if (users.containsKey(username))
            return "this username already exists";
        else {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonObject = objectMapper.readTree(new File("users" + "/users.json"));
            JsonNode newNode = objectMapper.createObjectNode().textNode(password);
            ((ObjectNode) jsonObject).set(username, newNode);
            objectMapper.writeValue(new File("users" + "/users.json"), jsonObject);
            users.put(username, password);
            return "Successful sign up";
        }
    }

    public List<String> getUsers() {
        return users.keySet().stream().toList();
    }
}
