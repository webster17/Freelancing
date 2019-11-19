package com.santosh.stock_market.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Record {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn
  @JsonIgnore
  private Scrip scrip;

  @Column
  private Float open;

  @Column
  private Float close;

  @Column
  private Float high;

  @Column
  private Float low;

  @Column
  private Float last;

  @Column
  private Float previousClose;

  @Column(length = 11)
  private long totalTradeQuantity;

  @Column
  private double totalTradeValue;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @Column(columnDefinition="DATE")
  @Temporal(TemporalType.DATE)
  private Date date;

  @Column(length = 7)
  private long totalTrades;


  public Record() {
  }

  public Record(Scrip scrip, float open, float high, float low, float close, float last, float previousClose, long totalTradeQuantity, double totalTradeValue, long totalTrades, Date date) {
    this.scrip = scrip;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.last = last;
    this.previousClose = previousClose;
    this.totalTradeQuantity = totalTradeQuantity;
    this.totalTradeValue = totalTradeValue;
    this.totalTrades = totalTrades;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Scrip getScrip() {
    return scrip;
  }

  public void setScrip(Scrip scrip) {
    this.scrip = scrip;
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
    this.previousClose = previousClose
    ;
  }

  public long getTotalTradeQuantity() {
    return totalTradeQuantity;
  }

  public void setTotalTradeQuantity(long totalTradeQuantity) {
    this.totalTradeQuantity = totalTradeQuantity;
  }

  public double getTotalTradeValue() {
    return totalTradeValue;
  }

  public void setTotalTradeValue(double totalTradeValue) {
    this.totalTradeValue = totalTradeValue;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public long getTotalTrades() {
    return totalTrades;
  }

  public void setTotalTrades(long totalTrades) {
    this.totalTrades = totalTrades;
  }

}