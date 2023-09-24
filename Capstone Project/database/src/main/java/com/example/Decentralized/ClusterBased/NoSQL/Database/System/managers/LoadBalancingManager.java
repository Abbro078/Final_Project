package com.example.Decentralized.ClusterBased.NoSQL.Database.System.managers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

@Component
@Data
@RequiredArgsConstructor
public class LoadBalancingManager {

    private final LinkedList<Long> requests;
    private final Timer timer;

    public LoadBalancingManager() {
        this.requests = new LinkedList<>();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }
        }, 0, 1000);
    }


    public synchronized void logRequest() {
        this.requests.add(System.currentTimeMillis());
    }

    public synchronized int getRequestsInLastMinute() {
        cleanup();
        return this.requests.size();
    }

    private void cleanup() {
        long oneMinuteAgo = System.currentTimeMillis() - 60000;
        while (!this.requests.isEmpty() && this.requests.peek() < oneMinuteAgo) {
            this.requests.pop();
        }
    }

    public boolean isRedirectRequired() {
        return getRequestsInLastMinute() > 5;
    }

}
