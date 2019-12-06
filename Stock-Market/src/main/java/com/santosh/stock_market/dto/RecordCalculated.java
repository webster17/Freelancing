package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.santosh.stock_market.utility.MoveType;
import com.santosh.stock_market.utility.State;
import com.santosh.stock_market.utility.config.ColourState;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties
public class RecordCalculated implements Serializable {

  private static DecimalFormat df = new DecimalFormat("0.00");
  public State state, closeState;
  public MoveType moveType;
  public String moveColour;
  private Long id;
  private String scripName;
  private Float high;
  private Float low;
  private Float range;
  private Float open;
  private boolean selected=false;

  public String getSelected() {
    if(selected && state==State.UP)
      return "high";
    else if(selected && state==State.DOWN)
      return "low";
    else
      return "-";
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  private Float close, previousClose;
  private Float last;
  private Float previousRange;
  private MovingAverage sma, ema39, ema61;
  private String gap;
  private Long totalTradeQuantity, totalTrades;
  private Double totalTradeValue;
  private String highLowArrow;
  private ColourState highLowArrowColour;
  @JsonFormat(pattern = "dd-MM-yyyy")
  private Date date;
  private List<Range> rangeList;

  public RecordCalculated(Long id, String scripName, Float high, Float low, Float open, Float close, Float last, Long totalTradeQuantity, Long totalTrades, Double totalTradeValue, Float smaValue, Float ema39Value, Float ema61Value, Date date) {
    this.id = id;
    this.scripName = scripName;
    this.high = Math.round(high * 100) / 100F;
    this.low = Math.round(low * 100) / 100F;
    this.open = Math.round(open * 100) / 100F;
    this.close = Math.round(close * 100) / 100F;
    this.last = Math.round(last * 100) / 100F;
    this.totalTradeQuantity = totalTradeQuantity;
    this.totalTrades = totalTrades;
    this.totalTradeValue = Math.round(totalTradeValue * 100) / 100.0;
    this.date = date;
    this.range = Math.round((high - low) * 100) / 100F;
    rangeList = new ArrayList<>();
    this.state = State.NONE;
    this.moveType = MoveType.BLANK;

    if (smaValue != null) {
      float value = Math.round(smaValue * 100) / 100F;
      String smaColour = "-";
      if (this.close > value)
        smaColour = "green";
      else if (this.close < value)
        smaColour = "red";
      else
        smaColour = "grey";
      this.sma = new MovingAverage(value, smaColour);
    }
    if (ema39Value != null) {
      float value = Math.round(ema39Value * 100) / 100F;
      String emaColour = "-";
      if (this.close > value)
        emaColour = "green";
      else if (this.close < value)
        emaColour = "red";
      else
        emaColour = "grey";
      this.ema39 = new MovingAverage(value, emaColour);
    }
    if (ema61Value != null) {
      float value = Math.round(ema61Value * 100) / 100F;
      String emaColour = "-";
      if (this.close > value)
        emaColour = "green";
      else if (this.close < value)
        emaColour = "red";
      else
        emaColour = "grey";
      this.ema61 = new MovingAverage(value, emaColour);
    }

  }

  public MovingAverage getSma() {
    return sma;
  }

  public Float getPreviousRange() {
    return previousRange;
  }

  public void setPreviousRange(Float previousRange) {
    this.previousRange = previousRange;
  }

  public Float getRange() {
    return range;
  }

  public List<Range> getRangeList() {
    return rangeList;
  }

  public String getGap() {
    return gap;
  }

  public MovingAverage getEma39() {
    return ema39;
  }

  public MovingAverage getEma61() {
    return ema61;
  }

  public void calculateHighLowArrow(RecordCalculated previousDayRecord, RecordCalculated mostRecentUpOrDownRecord) {

    highLowArrow = "-";
    gap = "-";
    closeState=State.NONE;
    previousClose = 0.0F;

    if (previousDayRecord != null) {

      if (close > previousDayRecord.getClose())
        closeState = State.UP;
      else if (close < previousDayRecord.getClose())
        closeState = State.DOWN;
      else
        closeState = State.NONE;

      previousClose = previousDayRecord.getClose();

      Range range1180 = new Range(11.80F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range2360 = new Range(23.60F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range3820 = new Range(38.20F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range6180 = new Range(61.80F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range100 = new Range(100F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range12720 = new Range(127.20F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range16180 = new Range(161.80F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range26180 = new Range(261.80F, previousDayRecord.getRange(), previousDayRecord.getClose());
      Range range42360 = new Range(423.60F, previousDayRecord.getRange(), previousDayRecord.getClose());

      range1180.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), null, range2360);
      range2360.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), null, null);
      range3820.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), range2360, range6180);
      range6180.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), range3820, range100);
      range100.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), range6180, range12720);
      range12720.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), range100, range16180);
      range16180.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), range12720, range26180);
      range26180.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), range16180, range42360);
      range42360.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), range26180, null);

      rangeList.add(range1180);
      rangeList.add(range2360);
      rangeList.add(range3820);
      rangeList.add(range6180);
      rangeList.add(range100);
      rangeList.add(range12720);
      rangeList.add(range16180);
      rangeList.add(range26180);
      rangeList.add(range42360);

      if (sma != null) {
        sma.setPosition(getPlottingPosition(sma.getValue(), State.UP), getPlottingPosition(sma.getValue(), State.DOWN));
      }

      if (previousDayRecord.getEma39() != null) {
        float value = Math.round(((close - previousDayRecord.getEma39().getValue()) * (2.0F / (39 + 1)) + previousDayRecord.getEma39().getValue()) * 100) / 100F;
        String emaColour = "-";
        if (this.close > value)
          emaColour = "green";
        else if (this.close < value)
          emaColour = "red";
        else
          emaColour = "grey";
        this.ema39 = new MovingAverage(value, emaColour);
      }

      if (previousDayRecord.getEma61() != null) {
        float value = Math.round(((close - previousDayRecord.getEma61().getValue()) * (2.0F / (61 + 1)) + previousDayRecord.getEma61().getValue()) * 100) / 100F;
        String emaColour = "-";
        if (this.close > value)
          emaColour = "green";
        else if (this.close < value)
          emaColour = "red";
        else
          emaColour = "grey";
        this.ema61 = new MovingAverage(value, emaColour);
      }

      if (ema39 != null) {
        ema39.setPosition(getPlottingPosition(ema39.getValue(), State.UP), getPlottingPosition(ema39.getValue(), State.DOWN));
      }
      if (ema61 != null) {
        ema61.setPosition(getPlottingPosition(ema61.getValue(), State.UP), getPlottingPosition(ema61.getValue(), State.DOWN));
      }

      if (low > previousDayRecord.getHigh()) {
        gap = "High";
      } else if (high < previousDayRecord.getLow()) {
        gap = "low";
      }

      if (this.close > previousDayRecord.getHigh()) {
        state = State.UP;
        highLowArrow = "Up";
      } else if (this.close < previousDayRecord.getLow()) {
        state = State.DOWN;
        highLowArrow = "Down";
      } else if (mostRecentUpOrDownRecord != null) {
        if (mostRecentUpOrDownRecord.state == State.UP) {
          if (high >= previousDayRecord.getHigh() && low >= previousDayRecord.getLow()) {
            state = State.UP;
            highLowArrow = "Up";
          } else if (this.high <= previousDayRecord.getHigh() && this.low <= previousDayRecord.getLow() && this.close >= previousDayRecord.getLow()) {
            state = State.UP;
            highLowArrow = "N-";
          } else if (this.high < previousDayRecord.getHigh() && this.low > previousDayRecord.getLow()) {
            state = State.UP;
//            && closeState == previousDayRecord.closeState
            highLowArrow = "|";
          } else if (this.high > previousDayRecord.getHigh() && this.low < previousDayRecord.getLow() && this.close > previousDayRecord.getLow()) {
            state = State.UP;
            highLowArrow = "N-Up";
          } else {
            state = State.NONE;
            highLowArrow = "-";
          }
        } else {
          if (high <= previousDayRecord.getHigh() && low <= previousDayRecord.getLow()) {
            state = State.DOWN;
            highLowArrow = "Down";
          } else if (this.low >= previousDayRecord.getLow() && this.high >= previousDayRecord.getHigh() && this.close <= previousDayRecord.getHigh()) {
            state = State.DOWN;
            highLowArrow = "-N";
          } else if (high < previousDayRecord.getHigh() && low > previousDayRecord.getLow()) {
            state = State.DOWN;
            highLowArrow = "|";
          } else if (this.low < previousDayRecord.getLow() && this.high > previousDayRecord.getHigh() && this.close < previousDayRecord.getHigh()) {
            state = State.DOWN;
            highLowArrow = "-NDown";
          } else {
            state = State.NONE;
            highLowArrow = "-";
            {
              state = State.NONE;
              highLowArrow = "-";
            }
          }
        }
      }
    }
  }

  public void calculateHighLowArrowColour(Range firstRange, Range secondRange, List<RecordCalculated> recordCalculatedList) {
    if (this.state == State.NONE) {
      this.highLowArrowColour = ColourState.GREY;
    } else if (firstRange != null && secondRange != null) {
      boolean isDarkColourEncountered = false;
      for (RecordCalculated recordCalculated : recordCalculatedList) {
        if (state == State.DOWN && recordCalculated.highLowArrowColour == ColourState.DARK_RED)
          isDarkColourEncountered = true;
        else if (state == State.UP && recordCalculated.highLowArrowColour == ColourState.DARK_GREEN)
          isDarkColourEncountered = true;
      }
      if (isDarkColourEncountered) {
        if (state == State.DOWN)
          this.highLowArrowColour = ColourState.DARK_RED;
        else
          this.highLowArrowColour = ColourState.DARK_GREEN;
      } else {
        if (state == State.UP && firstRange.getRisingChannel().getHigherTradedValue() != null && secondRange.getRisingChannel().getHigherTradedValue() != null) {
          if (high > firstRange.getRisingChannel().getHigherTradedValue() && high > secondRange.getRisingChannel().getHigherTradedValue()) {
            this.highLowArrowColour = ColourState.DARK_GREEN;
          } else {
            this.highLowArrowColour = ColourState.LIGHT_GREEN;
          }
        } else if (state == State.DOWN && firstRange.getFallingChannel().getLowerTradedValue() != null && secondRange.getFallingChannel().getLowerTradedValue() != null) {
          if (low < firstRange.getFallingChannel().getLowerTradedValue() && low < secondRange.getFallingChannel().getLowerTradedValue()) {
            this.highLowArrowColour = ColourState.DARK_RED;
          } else {
            this.highLowArrowColour = ColourState.LIGHT_RED;
          }
        } else {
          if (this.state == State.UP)
            this.highLowArrowColour = ColourState.LIGHT_GREEN;
          else
            this.highLowArrowColour = ColourState.LIGHT_RED;
        }
      }
    } else {
      if (this.state == State.UP)
        this.highLowArrowColour = ColourState.LIGHT_GREEN;
      else if (this.state == State.DOWN)
        this.highLowArrowColour = ColourState.LIGHT_RED;
      else
        this.highLowArrowColour = ColourState.GREY;
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getScripName() {
    return scripName;
  }

  public void setScripName(String scripName) {
    this.scripName = scripName;
  }

  public Float getHigh() {
    return high;
  }

  public void setHigh(Float high) {
    this.high = high;
  }

  public Range getTradedValue(float comparableValue, State channelState, int level) {

    int selectedRangeList = getPlottingPosition(comparableValue, channelState);
//
//    int selectedRangeList = -1;
//    if (state == State.UP) {
//      for (int i = 0; i < rangeList.size(); i++) {
//        if (i == 0) {
//          if (rangeList.get(i).getRisingChannel().getTradedValue() > comparableValue) {
//            selectedRangeList = i;
//            break;
//          }
//        } else if (i == rangeList.size() - 1) {
//          if (rangeList.get(i).getRisingChannel().getTradedValue() <= comparableValue) {
//            selectedRangeList = i;
//            break;
//          }
//        } else if (rangeList.get(i - 1).getRisingChannel().getTradedValue() <= comparableValue && rangeList.get(i).getRisingChannel().getTradedValue() > comparableValue) {
//          selectedRangeList = i;
//          break;
//        }
//      }
//    } else if (state == State.DOWN) {
//      for (int i = 0; i < rangeList.size(); i++) {
//        if (i == 0) {
//          if (rangeList.get(i).getFallingChannel().getTradedValue() < comparableValue) {
//            selectedRangeList = i;
//            break;
//          }
//        } else if (i == rangeList.size() - 1) {
//          if (rangeList.get(i).getFallingChannel().getTradedValue() >= comparableValue) {
//            selectedRangeList = i;
//            break;
//          }
//        } else if (rangeList.get(i - 1).getFallingChannel().getTradedValue() >= comparableValue && rangeList.get(i).getFallingChannel().getTradedValue() < comparableValue) {
//          selectedRangeList = i;
//          break;
//        }
//      }
//    }

    if (selectedRangeList != -1 && selectedRangeList < 2) {
      selectedRangeList = 2;
    }

    if (selectedRangeList >= 0 && selectedRangeList < rangeList.size()) {
      selectedRangeList = selectedRangeList + level;
    }

    if (selectedRangeList >= 0 && selectedRangeList < rangeList.size())
      return rangeList.get(selectedRangeList);
    return null;
  }

  public int getPlottingPosition(float comparableValue, State channelState) {
    if (channelState == State.UP) {
      for (int i = 0; i < rangeList.size(); i++) {
        if (i == 0) {
          if (comparableValue < rangeList.get(i).getRisingChannel().getTradedValue())
            return i;
        } else if (comparableValue >= rangeList.get(i - 1).getRisingChannel().getTradedValue() && comparableValue < rangeList.get(i).getRisingChannel().getTradedValue())
          return i;
        else if (i == rangeList.size() - 1 && comparableValue >= rangeList.get(i).getRisingChannel().getTradedValue())
          return i + 1;
      }
    } else if (channelState == State.DOWN) {
      for (int i = 0; i < rangeList.size(); i++) {
        if (i == 0) {
          if (comparableValue > rangeList.get(i).getFallingChannel().getTradedValue())
            return i;
        } else if (comparableValue <= rangeList.get(i - 1).getFallingChannel().getTradedValue() && comparableValue > rangeList.get(i).getFallingChannel().getTradedValue())
          return i;
        else if (i == rangeList.size() - 1 && comparableValue <= rangeList.get(i).getFallingChannel().getTradedValue())
          return i + 1;
      }
    }

    return -1;
  }

  public String getMoveType() {
    if (moveType == MoveType.JUSTIFIED_STRONG_DOWN)
      return "J Strong Down";
    else if (moveType == MoveType.JUSTIFIED_STRONG_UP)
      return "J Strong Up";
    else if (moveType == MoveType.STRONG_UP)
      return "Strong Up";
    else if (moveType == MoveType.STRONG_DOWN)
      return "Strong Down";
    else if (moveType == MoveType.WEAK_UP)
      return "Weak Up";
    else if (moveType == MoveType.WEAK_DOWN)
      return "Weak Down";
    else if (moveType == MoveType.FALSE_UP)
      return "False Up";
    else if (moveType == MoveType.FALSE_DOWN)
      return "False Down";
    else
      return "-";
  }

  public void setMoveType(MoveType moveType) {
    this.moveType = moveType;
  }

  public String getMoveColour() {
    if (moveType == MoveType.JUSTIFIED_STRONG_DOWN)
      return "#b20000";
    else if (moveType == MoveType.JUSTIFIED_STRONG_UP)
      return "#006600";
    else if (moveType == MoveType.STRONG_UP)
      return "#00b200";
    else if (moveType == MoveType.STRONG_DOWN)
      return "#ff3232";
    else
      return "grey";
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

  public String getHighLowArrow() {
    return highLowArrow;
  }

  public void setHighLowArrow(String highLowArrow) {
    this.highLowArrow = highLowArrow;
  }

  public String getHighLowArrowColour() {
    if (highLowArrowColour == ColourState.LIGHT_GREEN)
      return "lightgreen";
    else if (highLowArrowColour == ColourState.LIGHT_RED)
      return "#ff7f7f";
    else if (highLowArrowColour == ColourState.DARK_RED)
      return "#dc3545";
    else if (highLowArrowColour == ColourState.DARK_GREEN)
      return "#28a745";
    else
      return "grey";
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getCloseState() {
    if(closeState == State.UP)
      return "Up";
    else if(closeState == State.DOWN)
      return "Down";
    else
      return "-";
  }

  public Float getPreviousClose() {
    return previousClose;
  }

  public void calculateMoveType(Range firstRange, Range secondRange, RecordCalculated firstRecord, RecordCalculated
      secondRecord, State state, List<RecordCalculated> sameCycleRecords) {
    moveType = MoveType.BLANK;

    if (firstRange != null && secondRecord != null && state == State.UP) {
      if (high >= firstRange.getRisingChannel().getLowerTradedValue()) {
        if (secondRange != null) {
          if (high >= secondRange.getRisingChannel().getLowerTradedValue()) {
            boolean isAlreadyStrongUp = false;
            for (RecordCalculated recordCalculated : sameCycleRecords)
              if (recordCalculated.moveType == MoveType.STRONG_UP) {
                isAlreadyStrongUp = true;
                break;
              }

            if (isAlreadyStrongUp)
              moveType = MoveType.JUSTIFIED_STRONG_UP;
            else
              moveType = MoveType.STRONG_UP;
          } else if ((secondRecord.moveType == MoveType.STRONG_UP || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) && high < secondRange.getRisingChannel().getLowerTradedValue()) {
            moveType = MoveType.FALSE_UP;
          } else if ((secondRecord.moveType == MoveType.STRONG_DOWN || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN) && high < secondRange.getRisingChannel().getLowerTradedValue()) {
            moveType = MoveType.WEAK_UP;
          }
        } else {
          if (secondRecord.moveType == MoveType.STRONG_UP || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) {
            moveType = MoveType.FALSE_UP;
          } else if (secondRecord.moveType == MoveType.STRONG_DOWN || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN) {
            moveType = MoveType.WEAK_UP;
          }
        }
      }
    } else if (firstRange != null && secondRecord == null && state == State.UP) {
      if (high >= firstRange.getRisingChannel().getLowerTradedValue()) {
        boolean isAlreadyStrongUp = false;
        for (RecordCalculated recordCalculated : sameCycleRecords)
          if (recordCalculated.moveType == MoveType.STRONG_UP) {
            isAlreadyStrongUp = true;
            break;
          }

        if (isAlreadyStrongUp)
          moveType = MoveType.JUSTIFIED_STRONG_UP;
        else
          moveType = MoveType.STRONG_UP;
      }
    } else if (firstRange != null && secondRecord != null && state == State.DOWN) {
      if (low <= firstRange.getFallingChannel().getHigherTradedValue()) {
        if (secondRange != null) {
          if (low <= secondRange.getFallingChannel().getHigherTradedValue()) {
            boolean isAlreadyStrongDown = false;
            for (RecordCalculated recordCalculated : sameCycleRecords)
              if (recordCalculated.moveType == MoveType.STRONG_DOWN) {
                isAlreadyStrongDown = true;
                break;
              }

            if (isAlreadyStrongDown)
              moveType = MoveType.JUSTIFIED_STRONG_DOWN;
            else
              moveType = MoveType.STRONG_DOWN;
          } else if ((secondRecord.moveType == MoveType.STRONG_DOWN || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN) && low > secondRange.getFallingChannel().getHigherTradedValue()) {
            moveType = MoveType.FALSE_DOWN;
          } else if ((secondRecord.moveType == MoveType.STRONG_UP || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) && low > secondRange.getFallingChannel().getHigherTradedValue()) {
            moveType = MoveType.WEAK_DOWN;
          }
        } else {
          if (secondRecord.moveType == MoveType.STRONG_UP || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) {
            moveType = MoveType.WEAK_DOWN;
          } else if (secondRecord.moveType == MoveType.STRONG_DOWN || secondRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN) {
            moveType = MoveType.FALSE_DOWN;
          }
        }
      }
    } else if (firstRange != null && secondRecord == null && state == State.DOWN) {
      if (low <= firstRange.getFallingChannel().getHigherTradedValue()) {
        boolean isAlreadyStrongDown = false;
        for (RecordCalculated recordCalculated : sameCycleRecords)
          if (recordCalculated.moveType == MoveType.STRONG_DOWN) {
            isAlreadyStrongDown = true;
            break;
          }

        if (isAlreadyStrongDown)
          moveType = MoveType.JUSTIFIED_STRONG_DOWN;
        else
          moveType = MoveType.STRONG_DOWN;
      }
    }
  }

}