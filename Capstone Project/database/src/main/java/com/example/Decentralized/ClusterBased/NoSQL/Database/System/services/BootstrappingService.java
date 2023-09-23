package com.example.Decentralized.ClusterBased.NoSQL.Database.System.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Service
public class BootstrappingService {

    private final RestTemplate restTemplate=new RestTemplate();
    public void pingBootstrapperForInitData(){
        int bootstrapperPort = Integer.parseInt(System.getenv("Bootstrapper_Port"));
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("port", System.getenv("Node_Port"));
        var pingBootStrapperUrl = String.format("http://host.docker.internal:%s/database/init-ping", bootstrapperPort);
        HttpEntity<String> request =
                new HttpEntity<String>(jsonNode.toString(), headers);
        restTemplate.postForObject(pingBootStrapperUrl, request, String.class);
    }
}
