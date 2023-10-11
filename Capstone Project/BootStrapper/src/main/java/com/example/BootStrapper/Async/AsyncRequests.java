package com.example.BootStrapper.Async;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class AsyncRequests {
    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sync(String port) throws InterruptedException {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<String> ports = new ArrayList<>();
        for (int i = 8080; i < 8084; i++) {
            ports.add(String.valueOf(i));
        }
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ArrayNode arrayNode = ((ObjectNode) jsonNode).putArray("ports");
        ports.forEach(arrayNode::add);
        for (String portNum : ports) {
            var databaseNodeUrl = String.format("http://localhost:%s/bootstrapper/init-data", portNum);
            HttpEntity<String> request = new HttpEntity<>(jsonNode.toString(), headers);
            Thread.sleep(3000);
            restTemplate.postForObject(databaseNodeUrl, request, String.class);
        }
    }
}
