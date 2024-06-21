package com.example.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class LocalRedisConfig {
    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis(){
        System.out.println("Starting Redis...");
        redisServer = new RedisServer(redisPort);
        redisServer.start();
        System.out.println("Redis started on port: " + redisPort);
    }

    @PreDestroy
    public void stopRedis(){
        System.out.println("Stopping Redis...");
        if(redisServer != null){
            redisServer.stop();
        }
        System.out.println("Redis stopped.");

    }

}
