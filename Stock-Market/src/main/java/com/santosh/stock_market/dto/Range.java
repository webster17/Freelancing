package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties
public class Range implements Serializable {

  private float percentage;
  private Channel risingChannel, fallingChannel;


  public Range(Float percentage, Float range, Float close) {
    this.percentage = percentage;
    this.risingChannel = new Channel(percentage / 100, range, close, true);
    this.fallingChannel = new Channel(percentage / 100, range, close, false);
  }

  public void calculateHigherAndLowerTradedValue(Float close, Range previousRange, Range nextRange) {
    if (percentage == 23.60F) {
      this.risingChannel.setHigherTradedValue((float) Math.ceil((risingChannel.getTradedValue() + close * 0.00073F) * 20) / 20);
      this.risingChannel.setLowerTradedValue((float) Math.ceil((risingChannel.getTradedValue() - close * 0.00073F) * 20) / 20);
      this.fallingChannel.setHigherTradedValue((float) Math.ceil((fallingChannel.getTradedValue() + close * 0.00073F) * 20) / 20);
      this.fallingChannel.setLowerTradedValue((float) Math.floor((fallingChannel.getTradedValue() - close * 0.00073F) * 20) / 20);
    } else if (percentage == 11.80F && nextRange != null) {
      this.risingChannel.setHigherTradedValue((float) Math.ceil(((nextRange.getRisingChannel().getTradedValue() - risingChannel.getTradedValue()) * 0.382 + risingChannel.getTradedValue()) * 20) / 20);
      this.risingChannel.setLowerTradedValue((float) Math.floor((risingChannel.getTradedValue() - (risingChannel.getTradedValue() - fallingChannel.getTradedValue()) * 0.382) * 20) / 20);
      this.fallingChannel.setHigherTradedValue((float) Math.ceil(((risingChannel.getTradedValue() - fallingChannel.getTradedValue()) * 0.382 + fallingChannel.getTradedValue()) * 20) / 20);
      this.fallingChannel.setLowerTradedValue((float) Math.floor((fallingChannel.getTradedValue() - (fallingChannel.getTradedValue() - nextRange.fallingChannel.getTradedValue()) * 0.382) * 20) / 20);
    } else if (previousRange != null && nextRange != null) {
      this.risingChannel.setHigherTradedValue((float) Math.ceil(((nextRange.getRisingChannel().getTradedValue() - risingChannel.getTradedValue()) * 0.382 + risingChannel.getTradedValue()) * 20) / 20);
      this.risingChannel.setLowerTradedValue((float) Math.floor((risingChannel.getTradedValue() - (risingChannel.getTradedValue() - previousRange.getRisingChannel().getTradedValue()) * 0.382) * 20) / 20);
      this.fallingChannel.setHigherTradedValue((float) Math.ceil(((previousRange.getFallingChannel().getTradedValue() - fallingChannel.getTradedValue()) * 0.382 + fallingChannel.getTradedValue()) * 20) / 20);
      this.fallingChannel.setLowerTradedValue((float) Math.floor((fallingChannel.getTradedValue() - (fallingChannel.getTradedValue() - nextRange.getFallingChannel().getTradedValue()) * 0.382) * 20) / 20);
    }
  }

  public float getPercentage() {
    return percentage;
  }

  public Channel getRisingChannel() {
    return risingChannel;
  }

  public Channel getFallingChannel() {
    return fallingChannel;
  }

}