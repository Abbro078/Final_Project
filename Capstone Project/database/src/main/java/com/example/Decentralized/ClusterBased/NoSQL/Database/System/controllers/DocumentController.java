package com.example.Decentralized.ClusterBased.NoSQL.Database.System.controllers;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.*;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;


@RestController
@RequestMapping(path = "/document")
public class DocumentController {
    private final DocumentService documentService;
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private LoadBalancingManager loadBalancingManager;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("")
    public String createDocument(@RequestParam String databaseName, @RequestParam String collectionName, @RequestBody String jsonDocument, @RequestParam(required = false) Boolean propagate, @RequestParam(required = false) Boolean fromAffinityNode, Optional<String> id) throws IOException {
        if (ClusterManager.nodeIndex() == DatabaseManager.getAffinityNode(databaseName, collectionName) || (fromAffinityNode != null && fromAffinityNode)) {
            documentService.createDocument(databaseName, collectionName, jsonDocument, id);
            if (propagate == null || propagate) {
                if (loadBalancingManager.isRedirectRequired()) {
                    System.out.println("This request will be redirected to a different node");
                    String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/document", databaseName, collectionName, null, null, null, null);
                    restTemplate.postForObject(uri, jsonDocument, String.class);
                } else {
                    loadBalancingManager.logRequest();
                    for (String node : ClusterManager.getInstance().getPorts()) {
                        if (!node.equals(System.getenv("Node_Port"))) {
                            int lastId = DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocuments().size() - 1;
                            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://host.docker.internal:" + node + "/document").queryParam("databaseName", databaseName).queryParam("collectionName", collectionName).queryParam("fromAffinityNode", true).queryParam("id", DatabaseManager.getInstance().getDatabases().get(databaseName).getCollections().get(collectionName).getDocuments().get(lastId)).queryParam("propagate", false);
                            restTemplate.postForObject(builder.toUriString(), jsonDocument, String.class);
                        }
                    }
                }
            }
        } else {
            String uri = RequestManager.buildUri(ClusterManager.get(DatabaseManager.getAffinityNode(databaseName, collectionName)), "/document", databaseName, collectionName, null, null, null, null);
            restTemplate.postForObject(uri, jsonDocument, String.class);
        }
        return "Document created";
    }

    @GetMapping("/delete")
    public String deleteDocument(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String documentName, @RequestParam(required = false) Boolean propagate, @RequestParam(required = false) Boolean fromAffinityNode) {
        if (ClusterManager.nodeIndex() == DatabaseManager.getAffinityNode(databaseName, collectionName) || (fromAffinityNode != null && fromAffinityNode)) {
            documentService.deleteDocument(databaseName, collectionName, documentName);
            if (propagate == null || propagate) {
                if (loadBalancingManager.isRedirectRequired()) {
                    System.out.println("This request will be redirected to a different node");
                    String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/document/delete", databaseName, collectionName, documentName, null, null, null);
                    restTemplate.getForEntity(uri, null, String.class);
                } else {
                    for (String node : ClusterManager.getInstance().getPorts()) {
                        if (!node.equals(System.getenv("Node_Port"))) {
                            String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/document/delete", databaseName, collectionName, documentName, null, false, true);
                            restTemplate.getForEntity(uri, null, String.class);
                        }
                    }
                }
            }
        } else {
            String uri = RequestManager.buildUri(ClusterManager.get(DatabaseManager.getAffinityNode(databaseName, collectionName)), "/document/delete", databaseName, collectionName, documentName, null, null, null);
            restTemplate.getForEntity(uri, null, String.class);
        }
        return "Document deleted";
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam String databaseName, @RequestParam String collectionName) throws IOException {
        return ResponseEntity.ok(documentService.getAll(databaseName, collectionName));
    }

    @GetMapping("id")
    public ResponseEntity<?> getById(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String id) throws IOException {
        try {
            return ResponseEntity.ok(FileManager.getDocument(databaseName, collectionName, id));
        } catch (IOException e) {
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("update")
    public String updateDocument(@RequestParam String databaseName, @RequestParam String collectionName, @RequestParam String documentName, @RequestBody String jsonDocument, @RequestParam(required = false) Boolean propagate, @RequestParam(required = false) Boolean fromAffinityNode) {
        if (ClusterManager.nodeIndex() == DatabaseManager.getAffinityNode(databaseName, collectionName) || (fromAffinityNode != null && fromAffinityNode)) {
            documentService.updateDocument(databaseName, collectionName, documentName, jsonDocument);
            if (propagate == null || propagate) {
                if (loadBalancingManager.isRedirectRequired()) {
                    System.out.println("This request will be redirected to a different node");
                    String uri = RequestManager.buildUri(ClusterManager.get(ClusterManager.nextNode()), "/document/update", databaseName, collectionName, documentName, null, null, null);
                    restTemplate.postForObject(uri, jsonDocument, String.class);
                } else {
                    loadBalancingManager.logRequest();
                    for (String node : ClusterManager.getInstance().getPorts()) {
                        if (!node.equals(System.getenv("Node_Port"))) {
                            String uri = RequestManager.buildUri(node, "/document/update", databaseName, collectionName, documentName, null, false, true);
                            restTemplate.postForObject(uri, jsonDocument, String.class);
                        }
                    }
                }
            }
        } else {
            String uri = RequestManager.buildUri(ClusterManager.get(DatabaseManager.getAffinityNode(databaseName, collectionName)), "/document/update", databaseName, collectionName, documentName, null, null, null);
            restTemplate.postForObject(uri, jsonDocument, String.class);
        }
        return "Document has been updated";
    }

}
