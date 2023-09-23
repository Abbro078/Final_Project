package com.example.BootStrapper.controllers;

import ch.qos.logback.core.joran.sanity.Pair;
import com.example.BootStrapper.Async.AsyncRequests;
import com.example.BootStrapper.services.BootstrappingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.event.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("database")
@RequiredArgsConstructor
public class BootstrappingController {

    private final AsyncRequests asyncRequests;
    @Autowired
    private BootstrappingService bootstrappingService;



    @PostMapping("init-ping")
    public void getStartupMessage(@RequestBody InitPingMessage initPingMessage) throws InterruptedException {
        asyncRequests.sync(initPingMessage.getPort());
    }
    @GetMapping("users")
    public ResponseEntity getusers(){
        return ResponseEntity.ok(bootstrappingService.getUsers());
    }
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        if (!bootstrappingService.login(username, password)) {
            System.out.println("wrong credentials");
            KeyValuePair response=new KeyValuePair("credential",false);
            return ResponseEntity.ok(response);
        } else {
            KeyValuePair response=new KeyValuePair("credential",true);
            return ResponseEntity.ok(response);
        }
    }

}
@Data
class InitPingMessage{
    String port;
}