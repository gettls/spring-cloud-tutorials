package com.example.catalogsservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.catalogsservice.entity.CatalogEntity;

public interface CatalogRepository extends CrudRepository<CatalogEntity, Long>{
	CatalogEntity findByProductId(String productId);
}
