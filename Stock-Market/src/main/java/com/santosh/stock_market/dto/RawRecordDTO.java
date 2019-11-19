package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties
public class RawRecordDTO implements Serializable {

  private String scripName, isin, series, date;
  private Float high,low,open,close, last, previousClose;
  private Long totalTradeQuantity, totalTrades;
  private Double totalTradeValue;

  public String getScripName() {
    return scripName;
  }

  public void setScripName(String scripName) {
    this.scripName = scripName;
  }

  public String getIsin() {
    return isin;
  }

  public void setIsin(String isin) {
    this.isin = isin;
  }

  public String getSeries() {
    return series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Float getHigh() {
    return high;
  }

  public void setHigh(Float high) {
    this.high = high;
  }

  public Float getLow() {
    return low;
  }

  public void setLow(Float low) {
    this.low = low;
  }

  public Float getOpen() {
    return open;
  }

  public void setOpen(Float open) {
    this.open = open;
  }

  public Float getClose() {
    return close;
  }

  public void setClose(Float close) {
    this.close = close;
  }

  public Float getLast() {
    return last;
  }

  public void setLast(Float last) {
    this.last = last;
  }

  public Float getPreviousClose() {
    return previousClose;
  }

  public void setPreviousClose(Float previousClose) {
    this.previousClose = previousClose;
  }

  public Long getTotalTradeQuantity() {
    return totalTradeQuantity;
  }

  public void setTotalTradeQuantity(Long totalTradeQuantity) {
    this.totalTradeQuantity = totalTradeQuantity;
  }

  public Long getTotalTrades() {
    return totalTrades;
  }

  public void setTotalTrades(Long totalTrades) {
    this.totalTrades = totalTrades;
  }

  public Double getTotalTradeValue() {
    return totalTradeValue;
  }

  public void setTotalTradeValue(Double totalTradeValue) {
    this.totalTradeValue = totalTradeValue;
  }
}
