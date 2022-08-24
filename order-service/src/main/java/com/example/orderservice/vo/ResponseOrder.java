package com.example.orderservice.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ResponseOrder {
	private String productId;
	private Integer qty;
	private Integer unitPrice;
	private Integer totalPrice;
	private Date createdAt;
	
	private String orderId;
}
