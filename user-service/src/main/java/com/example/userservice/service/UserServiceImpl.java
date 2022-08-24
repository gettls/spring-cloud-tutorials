package com.example.userservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository; 
	private final BCryptPasswordEncoder passwordEncoder;
	private final RestTemplate restTemplate;
	private final Environment env;
	private final OrderServiceClient orderServiceClient;
	private final CircuitBreakerFactory circuitBreakerFactory;
	
	@Override
	public UserDto createUser(UserDto userDto) {
		userDto.setUserId(UUID.randomUUID().toString());
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = mapper.map(userDto, UserEntity.class);
		userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
		
		userRepository.save(userEntity);
		
		return null;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId); 
		
		if (userEntity == null) {
			throw new UsernameNotFoundException("User not found");
		}
		
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
							
//		List<ResponseOrder> orders = new ArrayList<>();
		/* Using as rest template */
//		String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//		ResponseEntity<List<ResponseOrder>> orderRespone =  
//				restTemplate.exchange(orderUrl, HttpMethod.GET, null, 
//						new ParameterizedTypeReference<List<ResponseOrder>>() {
//				});
		
//		List<ResponseOrder> ordersList = orderRespone.getBody();
		
		/* Using a Feign Client */  
		// + ErrorDecoder
//		List<ResponseOrder> ordersList = orderServiceClient.getOrders(userId);
//		userDto.setOrders(ordersList);
		
		log.info("Before call orders microservice");    
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
		List<ResponseOrder> ordersList = circuitBreaker.run(() -> orderServiceClient.getOrders(userId),
				throwable -> new ArrayList<>());
		log.info("After call orders microservice");    
		
		return userDto;
	}

	@Override
	public Iterable<UserDto> getUserByAll() {
		ModelMapper mapper = new ModelMapper();
		
		return Lists.newArrayList(userRepository.findAll()).stream()
					.map(u -> mapper.map(u, UserDto.class))
					.collect(Collectors.toList());
	}

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(username);
		if(userEntity == null)
			throw new UsernameNotFoundException(username);
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), 
				true, true, true, true,
				new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null){
			throw new UsernameNotFoundException(email);
		}
		
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		
		return userDto;
	}
	

}
