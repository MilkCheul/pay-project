package com.example.pay.investment.repo;

import org.springframework.data.repository.CrudRepository;

import com.example.pay.investment.model.ProductInfo;

public interface ProductInfoRedisRepository extends CrudRepository<ProductInfo, Integer> {
}