package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;

@JsonIgnoreProperties
public class MovingAverage implements Serializable {

  private String colour;
  private Float value;
  private HashMap<String, Integer> plot;

  public MovingAverage(Float value, String colour) {
    this.value = value;
    this.colour = colour;
    this.plot = new HashMap<>();
  }

  public void setPosition(int risingPosition, int fallingPosition) {
    if (risingPosition == 0 && fallingPosition == 0) {
      plot.put("rising", risingPosition);
      plot.put("falling", fallingPosition);
    } else if (risingPosition == 0) {
      plot.put("rising", -1);
      plot.put("falling", fallingPosition);
    } else if (fallingPosition == 0) {
      plot.put("rising", risingPosition);
      plot.put("falling", -1);
    } else {
      plot.put("rising", risingPosition);
      plot.put("falling", fallingPosition);
    }
  }

  public String getColour() {
    return colour;
  }

  public HashMap<String, Integer> getPlot() {
    return plot;
  }

  public Float getValue() {
    return value;
  }

}
