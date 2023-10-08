package com.example.BootStrapper.controllers;

import com.example.BootStrapper.Async.AsyncRequests;
import com.example.BootStrapper.services.BootstrappingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ResponseEntity getusers() {
        return ResponseEntity.ok(bootstrappingService.getUsers());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        if (!bootstrappingService.login(username, password)) {
            System.out.println("wrong credentials");
            KeyValuePair response = new KeyValuePair("credential", false);
            return (ResponseEntity<?>) ResponseEntity.badRequest();
        } else {
            KeyValuePair response = new KeyValuePair("credential", true);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) throws IOException {
        bootstrappingService.register(username, password);
        return ("register was successful i think");
    }

}

@Data
class InitPingMessage {
    String port;
}