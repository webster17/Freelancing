package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.santosh.stock_market.model.Scrip;

import java.util.List;

@JsonIgnoreProperties
public class ProfileScripDTO {
  private long id;
  private String  name;
  private List<Scrip> scrips;

  ProfileScripDTO(long id, String name, List<Scrip> scrips){
    this.id=id;
    this.name=name;
    this.scrips=scrips;
  }

}
