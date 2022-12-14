package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.error.FeignErrorDecoder;

import feign.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {OrderServiceClient.class})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate getrestRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public Logger.Level feignLoggerLevel(){
		return Logger.Level.FULL;
	}
	
	@Bean
	public FeignErrorDecoder feignErrorDecoder() {
		return new FeignErrorDecoder();
	}
}
  