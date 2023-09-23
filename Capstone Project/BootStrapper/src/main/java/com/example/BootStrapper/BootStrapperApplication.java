package com.example.BootStrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BootStrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootStrapperApplication.class, args);
	}

}
