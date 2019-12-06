package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties
public class PriceAdjustmentDTO implements Serializable {
  private long scripId;
  private int priceAdjustmentType;
  private int numerator;
  private int denominator;
  private String date;

  public long getScripId() {
    return scripId;
  }

  public void setScripId(long scripId) {
    this.scripId = scripId;
  }

  public int getPriceAdjustmentType() {
    return priceAdjustmentType;
  }

  public void setPriceAdjustmentType(int priceAdjustmentType) {
    this.priceAdjustmentType = priceAdjustmentType;
  }

  public int getNumerator() {
    return numerator;
  }

  public void setNumerator(int numerator) {
    this.numerator = numerator;
  }

  public int getDenominator() {
    return denominator;
  }

  public void setDenominator(int denominator) {
    this.denominator = denominator;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

}