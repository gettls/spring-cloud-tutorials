package com.example.userservice.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RequestUser {
	
	@NotNull(message = "Email can't be null")
	@Size(min = 2, message = "Email not be less than two chars")
	@Email
	private String email;
	
	@NotNull(message = "Name can't be null")
	@Size(min = 2, message = "Name not be less than two chars")
	private String name;
	
	@NotNull(message = "Password can't be null")
	@Size(min = 8, message = "Password not must be equal or greater than 8 chars")
	private String pwd;
	
}
