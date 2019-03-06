package com.phlink.example.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorRegistry;

@Configuration
public class GlobalInterceptorConfiguration {

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurerAdapter() {
        return new GlobalServerInterceptorConfigurer() {
            @Override
            public void addServerInterceptors(GlobalServerInterceptorRegistry registry) {
                registry.addServerInterceptors(new LogGrpcInterceptor());
            }
        };
    }

}