package com.santosh.stock_market.service;

import com.santosh.stock_market.dao.ScripRepository;
import com.santosh.stock_market.model.Scrip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScripService {

  @Autowired
  private ScripRepository scripRepository;

  public List<Scrip> getAllScripEntity(){
    List<Scrip> scripEntities = new ArrayList<>();
    scripRepository.findAll().forEach(scripEntities::add);
    return scripEntities;
  }

  public Optional<Scrip> getScrip(Long id){
    return scripRepository.findById(id);
  }

  public void addScripEntity(Scrip scripEntity){
    scripRepository.save(scripEntity);
  }

  public void updateScrip(Scrip scripEntity){
    scripRepository.save(scripEntity);
  }

  public void deleteScrip(Long id){
    scripRepository.deleteById(id);
  }

}