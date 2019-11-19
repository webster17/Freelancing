package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.santosh.stock_market.utility.MoveType;
import com.santosh.stock_market.utility.State;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties
public class RecordCalculated implements Serializable {

  private static DecimalFormat df = new DecimalFormat("0.00");
  public State state, closeState;
  public MoveType recordState;
  public MoveType moveType;
  private Long id;
  private String scripName;
  private Float high, low, range, open, close, last, previousClose, previousRange;
  private Long totalTradeQuantity, totalTrades;
  private Double totalTradeValue;
  private String highLowArrow, highLowArrowColour;
  @JsonFormat(pattern = "dd-MM-yyyy")
  private Date date;
  private List<Range> rangeList;

  public RecordCalculated(Long id, String scripName, Float high, Float low, Float open, Float close, Float last, Float previousClose, Long totalTradeQuantity, Long totalTrades, Double totalTradeValue, Date date) {
    this.id = id;
    this.scripName = scripName;
    this.high = Math.round(high * 100) / 100F;
    this.low = Math.round(low * 100) / 100F;
    this.open = Math.round(open * 100) / 100F;
    this.close = Math.round(close * 100) / 100F;
    this.last = Math.round(last * 100) / 100F;
    this.previousClose = Math.round(previousClose * 100) / 100F;
    this.totalTradeQuantity = totalTradeQuantity;
    this.totalTrades = totalTrades;
    this.totalTradeValue = Math.round(totalTradeValue * 100) / 100.0;
    this.date = date;
    this.range = Math.round((high - low) * 100) / 100F;
    rangeList = new ArrayList<>();
    this.state = State.NONE;
    this.recordState = MoveType.BLANK;


    if (close > previousClose)
      closeState = State.UP;
    else if (close < previousClose)
      closeState = State.DOWN;
    else
      closeState = State.NONE;
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

  public void calculateHighLowArrow(RecordCalculated previousDayRecord, RecordCalculated mostRecentUpOrDownRecord) {

    highLowArrow= "-";

    if (previousDayRecord != null) {

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
      range42360.calculateHigherAndLowerTradedValue(previousDayRecord.getClose(), null, null);

      rangeList.add(range1180);
      rangeList.add(range2360);
      rangeList.add(range3820);
      rangeList.add(range6180);
      rangeList.add(range100);
      rangeList.add(range12720);
      rangeList.add(range16180);
      rangeList.add(range26180);
      rangeList.add(range42360);

      if (this.close > previousDayRecord.getHigh()) {
        state = State.UP;
        highLowArrow = "Up";
      } else if (this.close < previousDayRecord.getLow()) {
        state = State.DOWN;
        highLowArrow = "Down";
      } else if (mostRecentUpOrDownRecord != null) {
        if (mostRecentUpOrDownRecord.state == State.UP) {
          if (high > previousDayRecord.getHigh() && low > previousDayRecord.getLow()) {
            state = State.UP;
            highLowArrow = "Up";
          } else if (this.high <= previousDayRecord.getHigh() && this.low <= previousDayRecord.getLow() && this.close >= previousDayRecord.getLow()) {
            state = State.UP;
            highLowArrow = "N-";
          } else if (this.high < previousDayRecord.getHigh() && this.low > previousDayRecord.getLow() ) {
            state = State.UP;
//            && closeState == previousDayRecord.closeState
            highLowArrow = "|";
          } else if (this.high > previousDayRecord.getHigh() && this.low < previousDayRecord.getLow() && this.close > previousDayRecord.getLow()) {
            state = State.UP;
            highLowArrow = "N-Up";
          }else {
            state = State.NONE;
            highLowArrow="-";
          }
        } else {
          if(high < previousDayRecord.getHigh() && low < previousDayRecord.getLow()){
            state = State.DOWN;
            highLowArrow = "Down";
          }
          else if (high < previousDayRecord.getHigh() && low > previousDayRecord.getLow()) {
            state = State.DOWN;
            highLowArrow = "|";
          } else if (this.low >= previousDayRecord.getLow() && this.high >= previousDayRecord.getHigh() && this.close <= previousDayRecord.getHigh()) {
            state = State.DOWN;
            highLowArrow = "-N";
          } else if (this.low < previousDayRecord.getLow() && this.high > previousDayRecord.getHigh() && this.close < previousDayRecord.getHigh()) {
            state = State.DOWN;
            highLowArrow = "-NDown";
          } else {
            state = State.NONE;
            highLowArrow="-";{
              state = State.NONE;
              highLowArrow = "-";
            }
          }
        }
      }
    } else

    System.out.println(highLowArrow);
  }

  public void calculateHighLowArrowColour(Range selectedRange, String previousRecordColour) {
    float selectedValue = 0F;
    if (state == State.UP) {
      if (selectedRange != null && selectedRange.getRisingChannel().getHigherTradedValue() != null)
        selectedValue = selectedRange.getRisingChannel().getHigherTradedValue();
      if (high > selectedValue)
        this.highLowArrowColour = "darkgreen";
      else
        this.highLowArrowColour = "lightgreen";
    } else if (state == State.DOWN) {
      if (selectedRange != null && selectedRange.getFallingChannel().getLowerTradedValue() != null)
        selectedValue = selectedRange.getFallingChannel().getLowerTradedValue();
      if (low > selectedValue)
        this.highLowArrowColour = "red";
      else
        this.highLowArrowColour = "darkred";
    } else {
      this.highLowArrowColour = previousRecordColour;
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

  public Range getTradedValue(float comparableValue, State state, int level) {
    int selectedRangeList = -1;
    if (state == State.UP) {
      for (int i = 0; i < rangeList.size(); i++) {
        if (i == 0) {
          if (rangeList.get(i).getRisingChannel().getTradedValue() > comparableValue) {
            selectedRangeList = i;
            break;
          }
        } else if (rangeList.get(i - 1).getRisingChannel().getTradedValue() <= comparableValue && rangeList.get(i).getRisingChannel().getTradedValue() > comparableValue) {
          selectedRangeList = i + level;
          break;
        } else if (i == rangeList.size() - 1) {
          if (rangeList.get(i).getRisingChannel().getTradedValue() <= comparableValue) {
            selectedRangeList = i + level;
            break;
          }
        }
      }
    } else if (state == State.DOWN) {
      for (int i = 0; i < rangeList.size(); i++) {
        if (i == 0) {
          if (rangeList.get(i).getFallingChannel().getTradedValue() < comparableValue) {
            selectedRangeList = i;
            break;
          }
        } else if (rangeList.get(i - 1).getFallingChannel().getTradedValue() >= comparableValue && rangeList.get(i).getFallingChannel().getTradedValue() < comparableValue) {
          selectedRangeList = i + level;
          break;
        } else if (i == rangeList.size() - 1) {
          if (rangeList.get(i).getFallingChannel().getTradedValue() >= comparableValue) {
            selectedRangeList = i + level;
            break;
          }
        }
      }
    }

    if (selectedRangeList != -1 && selectedRangeList < 2) {
      selectedRangeList = 2;
    }

    if (selectedRangeList > -1 && selectedRangeList < rangeList.size()) {
      selectedRangeList = selectedRangeList + level;
    }

    if (selectedRangeList > -1 && selectedRangeList < rangeList.size())
      return rangeList.get(selectedRangeList);
    return null;
  }

  public String getMoveType() {
    if (moveType == MoveType.JUSTIFIED_STRONG_DOWN)
      return "Justified Strong Down";
    else if (moveType == MoveType.JUSTIFIED_STRONG_UP)
      return "Justified Strong Up";
    else if (moveType == MoveType.STRONG_DOWN)
      return "Strong Down";
    else if (moveType == MoveType.STRONG_UP)
      return "Strong Up";
    else if (moveType == MoveType.WEAK_DOWN)
      return "Weak Down";
    else if (moveType == MoveType.WEAK_UP)
      return "Weak Up";
    else
      return "-";
  }

  public void setMoveType(MoveType moveType) {
    this.moveType = moveType;
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

  public String getHighLowArrow() {
    return highLowArrow;
  }

  public void setHighLowArrow(String highLowArrow) {
    this.highLowArrow = highLowArrow;
  }

  public String getHighLowArrowColour() {
    return highLowArrowColour;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getRecordState() {
    if (recordState == MoveType.JUSTIFIED_STRONG_DOWN)
      return "JSD";
    else if (recordState == MoveType.JUSTIFIED_STRONG_UP)
      return "JSU";
    else if (recordState == MoveType.STRONG_UP)
      return "SU";
    else if (recordState == MoveType.STRONG_DOWN)
      return "SD";
    else if (recordState == MoveType.WEAK_UP)
      return "WU";
    else if (recordState == MoveType.WEAK_DOWN)
      return "WD";
    else if (recordState == MoveType.FALSE_UP)
      return "FU";
    else if (recordState == MoveType.FALSE_DOWN)
      return "FD";
    else
      return "-";
  }

  public void calculateMoveType(Range firstRange, Range secondRange, RecordCalculated firstRecord, RecordCalculated secondRecord, State state, List<RecordCalculated> sameCycleRecords) {
    if (firstRange != null && secondRange != null && state == State.UP) {

      if (firstRange.getRisingChannel().getLowerTradedValue() != null && secondRange.getRisingChannel().getLowerTradedValue() != null) {

        if (high > firstRange.getRisingChannel().getLowerTradedValue() && high > secondRange.getRisingChannel().getLowerTradedValue()) {
          boolean isAlreadyStrongUp = false;
          for (RecordCalculated recordCalculated : sameCycleRecords)
            if (recordCalculated.moveType == MoveType.STRONG_UP)
              isAlreadyStrongUp = true;

          if (isAlreadyStrongUp)
            moveType = MoveType.JUSTIFIED_STRONG_UP;
          else
            moveType = MoveType.STRONG_UP;
        } else if (high <= secondRange.getRisingChannel().getLowerTradedValue() && secondRecord.state == State.UP) {
          moveType = MoveType.FALSE_UP;
        } else if (high < firstRange.getRisingChannel().getLowerTradedValue()) {
          moveType = MoveType.WEAK_UP;
        } else {
          moveType = MoveType.BLANK;
        }
      } else {
        moveType = MoveType.BLANK;
      }
    } else if (firstRange != null && state == State.UP) {
      if (firstRange.getRisingChannel() != null) {
        if (firstRange.getRisingChannel().getLowerTradedValue() != null && high > firstRange.getRisingChannel().getLowerTradedValue()) {
          boolean isAlreadyStrongUp = false;
          if (high > firstRange.getRisingChannel().getLowerTradedValue())
            for (RecordCalculated recordCalculated : sameCycleRecords)
              if (recordCalculated.moveType == MoveType.STRONG_UP)
                isAlreadyStrongUp = true;

          if (isAlreadyStrongUp)
            moveType = MoveType.JUSTIFIED_STRONG_UP;
          else
            moveType = MoveType.STRONG_UP;
        }
      } else {
        moveType = MoveType.BLANK;
      }
    } else if (firstRange != null && secondRange != null && state == State.DOWN) {
      if (firstRange.getFallingChannel().getHigherTradedValue() != null && secondRange.getFallingChannel().getHigherTradedValue() != null) {
        if (low < firstRange.getFallingChannel().getHigherTradedValue() && low < secondRange.getFallingChannel().getHigherTradedValue()) {
          boolean isAlreadyStrongDown = false;
          for (RecordCalculated recordCalculated : sameCycleRecords)
            if (recordCalculated.moveType == MoveType.STRONG_UP)
              isAlreadyStrongDown = true;

          if (isAlreadyStrongDown)
            moveType = MoveType.JUSTIFIED_STRONG_DOWN;
          else
            moveType = MoveType.STRONG_DOWN;
        } else if (low >= secondRange.getFallingChannel().getHigherTradedValue() && secondRecord.state == State.DOWN) {
          moveType = MoveType.FALSE_DOWN;
        } else if (low < firstRange.getFallingChannel().getHigherTradedValue()) {
          moveType = MoveType.WEAK_DOWN;
        } else {
          moveType = MoveType.BLANK;
        }
      } else {
        moveType = MoveType.BLANK;
      }

    } else if (firstRange != null && state == State.DOWN) {
      if (firstRange.getFallingChannel() != null) {
        if (firstRange.getFallingChannel().getHigherTradedValue() != null && low < firstRange.getFallingChannel().getHigherTradedValue()) {
          boolean isAlreadyStrongDown = false;
          for (RecordCalculated recordCalculated : sameCycleRecords)
            if (recordCalculated.moveType == MoveType.STRONG_UP)
              isAlreadyStrongDown = true;
          if (isAlreadyStrongDown)
            moveType = MoveType.JUSTIFIED_STRONG_DOWN;
          else
            moveType = MoveType.STRONG_DOWN;
        } else {
          moveType = MoveType.BLANK;
        }
      } else {
        moveType = MoveType.BLANK;
      }

    } else {
      moveType = MoveType.BLANK;
    }
  }
}