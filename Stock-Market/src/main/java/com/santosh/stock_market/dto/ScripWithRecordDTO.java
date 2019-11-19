package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.santosh.stock_market.model.Record;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties
public class ScripWithRecordDTO implements Serializable {

  private Long id;
  private String name, isin, series;
  private List<RecordCalculated> records;

  public ScripWithRecordDTO(Long id, String name, String isin, String series, List<RecordCalculated> rawRecords) {
    this.id = id;
    this.name = name;
    this.isin = isin;
    this.series = series;
    this.records = rawRecords;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public List<RecordCalculated> getRecords() {
    return records;
  }

  public void setRecords(List<RecordCalculated> records) {
    this.records = records;
  }
}
