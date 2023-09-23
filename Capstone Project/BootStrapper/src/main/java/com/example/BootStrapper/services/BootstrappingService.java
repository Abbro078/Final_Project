package com.example.BootStrapper.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class BootstrappingService {
    private final Map<String, String> users;

    public BootstrappingService() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        users = objectMapper.readValue(new File("users"+"/users.json"), Map.class);
    }

    public boolean login(String username, String password) {
        System.out.println(users);
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public List<String> getUsers(){
        return users.keySet().stream().toList();
    }
}
