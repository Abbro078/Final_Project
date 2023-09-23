package com.example.Decentralized.ClusterBased.NoSQL.Database.System.controllers;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.ClusterManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.LoadBalancingManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.RequestManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.services.DatabaseService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping(path = "/database")
public class DatabaseController {
    private final DatabaseService dataBaseService;
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private LoadBalancingManager loadBalancingManager;


    @Autowired
    public DatabaseController(DatabaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    @PostMapping("")
    public String createDatabase(@RequestParam String databaseName, @RequestParam(required = false) Boolean propagate) throws IOException {
        dataBaseService.createDatabase(databaseName);
        if (propagate == null || propagate) {
            if(loadBalancingManager.isRedirectRequired()) {
                System.out.println("This request will be redirected to a different node");
                String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/database", databaseName,null,null,null,null, null);
                restTemplate.postForEntity(uri, null, String.class);
            } else{
                loadBalancingManager.logRequest();
                for (String node : ClusterManager.getInstance().getPorts()) {
                    if (!node.equals(System.getenv("Node_Port"))) {
                        String uri = RequestManager.buildUri(node, "/database", databaseName, null, null, null, false, null);
                        restTemplate.postForEntity(uri, null, String.class);
                    }
                }
            }
        }
        return "Database created";
    }
    @GetMapping("/delete")
    public String deleteDatabase(@RequestParam String databaseName, @RequestParam(required = false) Boolean propagate) {
        System.out.println("delete"+databaseName);
        dataBaseService.deleteDatabase(databaseName);
        if (propagate == null || propagate) {
            if(loadBalancingManager.isRedirectRequired()) {
                System.out.println("This request will be redirected to a different node");
                String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/database/delete", databaseName,null, null, null, null, null);
                restTemplate.getForEntity(uri, null, String.class);
            } else {
                loadBalancingManager.logRequest();
                for (String node : ClusterManager.getInstance().getPorts()) {
                    if (!node.equals(System.getenv("Node_Port"))) {
                        String uri = RequestManager.buildUri(node, "/database/delete", databaseName, null, null, null, false, null);
                        restTemplate.getForEntity(uri, null, String.class);
                    }
                }
            }
        }
        return "Database deleted";
    }
    @GetMapping("")
    public ResponseEntity<?> getDatabases(){
        return ResponseEntity.ok(dataBaseService.getDatabases());
    }



}
