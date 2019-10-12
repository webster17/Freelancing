package com.santosh.stock_market.dao;

import com.santosh.stock_market.model.Scrip;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScripRepository extends CrudRepository<Scrip, Long> {

}