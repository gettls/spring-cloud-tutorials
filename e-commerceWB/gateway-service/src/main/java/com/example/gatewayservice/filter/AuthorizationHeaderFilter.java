package com.example.gatewayservice.filter;

import java.security.Key;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{

	private final Environment env;
	
	public AuthorizationHeaderFilter(Environment env) {
		super(Config.class);
		this.env = env;
	}
	
	@Data
	public static class Config {
		
		
	}
	
	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			
			if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replaceAll("Bearer", "");
			
			if(!isJwtValid(jwt)) {
				return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			return chain.filter(exchange);
		});
	}

	private boolean isJwtValid(String jwt) {
		boolean returnValue = true;
		
		String subject = null;
		try {
			subject = Jwts.parserBuilder()
						.setSigningKey(getSigningKey())
						.build()
						.parseClaimsJws(jwt).getBody()
						.getSubject();
		}catch (Exception e) {
			returnValue = false;
		}
		
		if(subject == null || subject.isEmpty()) {
			returnValue = false;
		}
		
		return returnValue;
	}
	
	private Key getSigningKey() {
		byte[] keyBytes = env.getProperty("token.secret").getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus){
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		
		log.error(error);
		return response.setComplete();
	}

}
