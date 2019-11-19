package com.santosh.stock_market.service;

import com.santosh.stock_market.dao.ScripRepository;
import com.santosh.stock_market.model.Scrip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScripService {

  @Autowired
  private ScripRepository scripRepository;

  public List<Scrip> getScrips(){
    return scripRepository.findAll();
  }

  public Optional<Scrip> findById(Long id){
    return scripRepository.findById(id);
  }

  public Optional<Scrip> findByIsin(String isin){
    return scripRepository.findByIsin(isin);
  }

  public List<Scrip> findByIdIn(List<Long> ids){
    return scripRepository.findByIdIn(ids);
  }

  public List<Scrip> findByName(String name){
    return scripRepository.findByName(name);
  }

  public List<Scrip> getAllScripSorted() {
    return scripRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
  }

  public void save(Scrip scrip){
    scripRepository.save(scrip);
  }

  public void update(Scrip scrip){
    scripRepository.save(scrip);
  }

  public void delete(Long id){
    scripRepository.deleteById(id);
  }

}