package com.example.BootStrapper.cluster;

import com.example.BootStrapper.shell.Shell;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@Component
public class BootStrap {

    @PostConstruct
    public static void init() throws IOException, ExecutionException, InterruptedException, TimeoutException {

        Shell.getInstance().runShellCommand("docker network create NoSqlDbNetwork");

        for (int i = 8080; i < 8084; i++) {
            String runContainer = String.format("docker run -d -p %s:9000 --network NoSqlDbNetwork -e Bootstrapper_Port=8000 -e Node_Port=%s --name container%s database_node", i, i, i - 8079);
            Shell.getInstance().runShellCommand(runContainer);
        }
    }
}



