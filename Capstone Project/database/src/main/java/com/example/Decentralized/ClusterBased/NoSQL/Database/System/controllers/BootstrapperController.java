package com.example.Decentralized.ClusterBased.NoSQL.Database.System.controllers;

import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Collection;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.Database.Database;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.ClusterManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers.DatabaseManager;
import com.example.Decentralized.ClusterBased.NoSQL.Database.System.requests.InitData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("bootstrapper")
@RestController
public class BootstrapperController {

    @PostMapping("init-data")
    public void getInitData(@RequestBody InitData initData){
        ClusterManager.getInstance().setPorts(initData.getPorts());
    }
    @GetMapping("all")
    public ResponseEntity<?> getAllData(){
        int databases=0;
        int collections=0;
        int documents=0;
        databases=DatabaseManager.getInstance().getDatabases().keySet().size();
        for(Database database: DatabaseManager.getInstance().getDatabases().values()){
            collections+=(database.getCollections().size());
            for(Collection collection: database.getCollections().values()){
                documents+=collection.getDocuments().size();
            }
        }
        Map<String,Integer> response=new HashMap<>();
        response.put("databases",databases);
        response.put("collections",collections);
        response.put("documents",documents);
        return ResponseEntity.ok(response);
    }
}
