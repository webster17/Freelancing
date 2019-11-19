package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties
public class Channel implements Serializable {

  private Float higherTradedValue, tradedValue, lowerTradedValue;

  public Channel(Float percentage,Float range, Float close, boolean isPositive){
    if(isPositive)
      tradedValue = (float)Math.ceil(((range*percentage)+close)*20)/20;
    else
      tradedValue = Math.abs((float)Math.ceil(((range*percentage)-close)*20)/20);
  }

  public void setHigherTradedValue(float higherTradedValue) {
    this.higherTradedValue = higherTradedValue;
  }

  public void setLowerTradedValue(float lowerTradedValue) {
    this.lowerTradedValue = lowerTradedValue;
  }


  public Float getHigherTradedValue() {
    return higherTradedValue;
  }
  public Float getTradedValue() {
    return tradedValue;
  }


  public Float getLowerTradedValue() {
    return lowerTradedValue;
  }
}
