package com.phlink.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wen
 * @create 2019-02-15 17:30
 */
@SpringBootApplication
public class LocalGrpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalGrpcServerApplication.class, args);
    }
}
