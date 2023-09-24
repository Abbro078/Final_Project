package com.example.Decentralized.ClusterBased.NoSQL.Database.System.controllers;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.ClusterManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.LoadBalancingManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.RequestManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.services.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(path = "/collection")
public class CollectionController {
    private final CollectionService collectionService;
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private LoadBalancingManager loadBalancingManager;


    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PostMapping("")
    public ResponseEntity createCollection(@RequestParam String databaseName, @RequestParam String collectionName, @RequestBody String schema, @RequestParam(required = false) Boolean propagate) throws IOException {
        collectionService.createCollection(databaseName, collectionName, schema);
        if (propagate == null || propagate) {
            if(loadBalancingManager.isRedirectRequired()) {
                System.out.println("This request will be redirected to a different node");
                String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/collection", databaseName, collectionName, null, null, null, null);
                restTemplate.postForObject(uri, schema, String.class);
            } else {
                loadBalancingManager.logRequest();
                for (String node : ClusterManager.getInstance().getPorts()) {
                    if (!node.equals(System.getenv("Node_Port"))) {
                        String uri = RequestManager.buildUri(node, "/collection", databaseName, collectionName, null, null,false,null);
                        restTemplate.postForObject(uri, schema, String.class);
                    }
                }
            }
        }
        return ResponseEntity.ok("Collection Created");
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestParam String databaseName){
        return ResponseEntity.ok(collectionService.getCollections(databaseName));
    }

    @GetMapping("/schema")
    public ResponseEntity<?> getSchema(@RequestParam String databaseName,@RequestParam String collectionName) throws IOException {
        return ResponseEntity.ok(collectionService.getSchema(databaseName,collectionName));
    }
    @PutMapping
    public String newIndex(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String key, @RequestParam(required = false) Boolean propagate, @RequestParam(required = false) Boolean fromAffinityNode) throws IOException {

        if(ClusterManager.nodeIndex() == DatabaseManager.getAffinityNode(databaseName, collectionName) || (fromAffinityNode != null && fromAffinityNode)) {
            collectionService.newIndex(databaseName, collectionName, key);
            if (propagate == null || propagate) {
                if(loadBalancingManager.isRedirectRequired()) {
                    System.out.println("This request will be redirected to a different node");
                    String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/collection", databaseName, collectionName, null, key, null, null);
                    restTemplate.put(uri, null, String.class);
                } else {
                    loadBalancingManager.logRequest();
                    for (String node : ClusterManager.getInstance().getPorts()) {
                        if (!node.equals(System.getenv("Node_Port"))) {
                            String uri = RequestManager.buildUri(node, "/collection", databaseName, collectionName, null, key, false, true);
                            restTemplate.put(uri, null, String.class);
                        }
                    }
                }
            }
        } else {
            String uri = RequestManager.buildUri(ClusterManager.get(DatabaseManager.getAffinityNode(databaseName,collectionName)), "/collection", databaseName, collectionName, null, key, null, null);
            restTemplate.put(uri, null, String.class);
        }
        return "New key indexed";
    }

    @GetMapping("/key")
    public ResponseEntity<?> filterByKey(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String key) throws IOException {
        return ResponseEntity.ok(collectionService.filterByKey(databaseName, collectionName, key));
    }

    @GetMapping("/value")
    public ResponseEntity<?> filterByValue(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String key, @RequestParam String value) throws IOException {
        return ResponseEntity.ok(collectionService.filterByValue(databaseName, collectionName, key, value));
    }

    @GetMapping("/delete")
    public String deleteCollection(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam(required = false) Boolean propagate, @RequestParam(required = false) Boolean fromAffinityNode) {
        if(ClusterManager.nodeIndex() == DatabaseManager.getAffinityNode(databaseName, collectionName) || (fromAffinityNode != null && fromAffinityNode)) {
            collectionService.deleteCollection(databaseName, collectionName);
            if (propagate == null || propagate) {
                if(loadBalancingManager.isRedirectRequired()) {
                    System.out.println("This request will be redirected to a different node");
                    String uri = RequestManager.buildUri( ClusterManager.get(ClusterManager.nextNode()), "/collection/delete", databaseName, collectionName, null,null,null,null);
                    restTemplate.getForEntity(uri, null, String.class);
                } else {
                    loadBalancingManager.logRequest();
                    for (String node : ClusterManager.getInstance().getPorts()) {
                        if (!node.equals(System.getenv("Node_Port"))) {
                            String uri = RequestManager.buildUri(node, "/collection/delete", databaseName, collectionName, null, null, false, true);
                            restTemplate.getForEntity(uri, null, String.class);
                        }
                    }
                }
            }
        } else {
            String uri = RequestManager.buildUri( ClusterManager.get(ClusterManager.nextNode()), "/collection/delete", databaseName, collectionName,null, null,null,null);
            restTemplate.getForEntity(uri, null, String.class);
        }
        return "Collection deleted";
    }



}
