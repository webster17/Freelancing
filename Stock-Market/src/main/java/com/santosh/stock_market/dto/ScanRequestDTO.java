package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class ScanRequestDTO {

  private String startDate, endDate, timeFrame;

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getTimeFrame() {
    return timeFrame;
  }

  public void setTimeFrame(String timeFrame) {
    this.timeFrame = timeFrame;
  }

}