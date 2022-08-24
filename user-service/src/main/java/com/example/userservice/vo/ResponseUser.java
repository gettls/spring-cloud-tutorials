package com.example.userservice.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ResponseUser {
	private String email;
	private String name;
	private String userId;
	
	private List<ResponseOrder> orders;
}
