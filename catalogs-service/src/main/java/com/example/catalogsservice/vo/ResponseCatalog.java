package com.example.catalogsservice.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ResponseCatalog {
	private String productId;
	private String productName;
	private Integer unitPrice;
	private Integer stock;
	private Date createdAt;
}
