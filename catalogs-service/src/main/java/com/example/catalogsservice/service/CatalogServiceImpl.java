package com.example.catalogsservice.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.catalogsservice.dto.CatalogDto;
import com.example.catalogsservice.entity.CatalogEntity;
import com.example.catalogsservice.repository.CatalogRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService{

	private final CatalogRepository catalogRepository;
	
	@Override
	public Iterable<CatalogEntity> getAllCatalogs() {
		 return catalogRepository.findAll();
	}

}
