package com.santosh.stock_market.controller;

import com.santosh.stock_market.dto.*;
import com.santosh.stock_market.model.Record;
import com.santosh.stock_market.model.Scrip;
import com.santosh.stock_market.service.RecordService;
import com.santosh.stock_market.service.ScripService;
import com.santosh.stock_market.utility.MoveType;
import com.santosh.stock_market.utility.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/record")
public class RecordController {

  @Autowired
  private ScripService scripService;

  @Autowired
  private RecordService recordService;

  @RequestMapping(value = "/update/{date}", method = RequestMethod.POST)
  public ResponseEntity<?> updateRecordOfDate(@PathVariable String date, @RequestBody List<RawRecordDTO> rawRecordDTOS) {
    Map<String, String> responseData = new HashMap<>();
    date = date.substring(0, 3) + date.substring(3, 4).toUpperCase() + date.toLowerCase().substring(4);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
    Date recordDate = Date.from(LocalDate.parse(date, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant());
    recordService.deleteRecordByDate(recordDate);

    if (recordDate != null) {
      for (RawRecordDTO rawRecordDto : rawRecordDTOS) {
        if(rawRecordDto.getSeries().equalsIgnoreCase("EQ") || rawRecordDto.getSeries().equalsIgnoreCase("BE")) {
          Optional<Scrip> scrip = scripService.findByIsin(rawRecordDto.getIsin());
          if (scrip.isEmpty()) {
            scripService.save(new Scrip(rawRecordDto.getScripName().toUpperCase(), rawRecordDto.getIsin().toUpperCase(), rawRecordDto.getSeries().toUpperCase()));
            scrip = scripService.findByIsin(rawRecordDto.getIsin());
          }
          if (scrip.isPresent()) {
            Record rawRecord = new Record(scrip.get(), rawRecordDto.getOpen(), rawRecordDto.getHigh(), rawRecordDto.getLow(), rawRecordDto.getClose(), rawRecordDto.getLast(), rawRecordDto.getPreviousClose(), rawRecordDto.getTotalTradeQuantity(), rawRecordDto.getTotalTradeValue(), rawRecordDto.getTotalTrades(), recordDate);
            recordService.save(rawRecord);
          }
        }
      }

      responseData.put("message", "Record has been successfully updated of " + date);
      return ResponseEntity.ok().body(responseData);
    } else {
      responseData.put("message", "Record date is not valid");
      return ResponseEntity.badRequest().body(responseData);
    }
  }

  @RequestMapping(value = "/filter/{scripId}", method = RequestMethod.POST)
  public ResponseEntity<?> scanRecord(@PathVariable @NonNull Long scripId, @RequestBody ScanRequestDTO scanRequestDTO) {
    try {
      Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(scanRequestDTO.getStartDate());
      Date endDate = new SimpleDateFormat("dd-MM-yyyy").parse(scanRequestDTO.getEndDate());
      Optional<Scrip> optionalScrip = scripService.findById(scripId);
      if (optionalScrip.isPresent()) {
        Scrip scrip = optionalScrip.get();
        List<Record> records = recordService.findByScripIdAndDateBetween(scripId, startDate, endDate);
        List<RecordCalculated> recordCalculatedList = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
          Record record = records.get(i);
          Float smaValue = null;
          Float ema39Value = null;
          Float ema61Value = null;
          if (i >= 40) {
            Float totalClose = 0.0F;
            for (int j = i; j > i - 41; j--)
              totalClose = totalClose + records.get(j).getClose();
            smaValue = totalClose / 41;
          }
          if (i == 38) {
            Float totalClose = 0.0F;
            for (int j = i; j > i - 39; j--) {
              totalClose = totalClose + records.get(j).getClose();
            }

            ema39Value = totalClose / 39;
          }
          if (i == 60) {
            Float totalClose = 0.0F;
            for (int j = i; j > i - 61; j--)
              totalClose = totalClose + records.get(j).getClose();
            ema61Value = totalClose / 61;
          }
          recordCalculatedList.add(new RecordCalculated(record.getId(), record.getScrip().getName(), record.getHigh(), record.getLow(), record.getOpen(), record.getClose(), record.getLast(), record.getTotalTradeQuantity(), record.getTotalTrades(), record.getTotalTradeValue(), smaValue, ema39Value, ema61Value, record.getDate()));
        }
        if (recordCalculatedList.size() > 0) {
          recordCalculatedList.get(0).calculateHighLowArrow(null, null);
          recordCalculatedList.get(0).calculateHighLowArrowColour(null, null, new ArrayList<>());


          for (int i = 1; i < recordCalculatedList.size(); i++) {

            RecordCalculated currentRecord = recordCalculatedList.get(i);
            RecordCalculated previousRecord = recordCalculatedList.get(i - 1);

            RecordCalculated mostRecentUpOrDownRecord = null;
            for (int k = i - 1; k >= 0; k--) {
              RecordCalculated recordCalculated = recordCalculatedList.get(k);
              if (recordCalculatedList.get(k).getHighLowArrow().equals("Up") || recordCalculatedList.get(k).getHighLowArrow().equals("Down")) {
                mostRecentUpOrDownRecord = recordCalculatedList.get(k);
                break;
              }
            }

            currentRecord.calculateHighLowArrow(previousRecord, mostRecentUpOrDownRecord);
            currentRecord.setPreviousRange(previousRecord.getRange());

            if (currentRecord.state == State.UP) {
              RecordCalculated firstRecordForColourState = null, firstRecordForMoveType = null, secondRecordForColourState = null, secondRecordForMoveType = null;
              Range firstRangeForMoveType = null, firstRangeForColourState = null, secondRangeForColourState = null, secondRangeForMoveType = null;

              List<RecordCalculated> sameMoveCycleList = new ArrayList<>();
              for (int j = i - 1; j >= 0; j--) {
                if (recordCalculatedList.get(j).state != currentRecord.state)
                  break;
                sameMoveCycleList.add(recordCalculatedList.get(j));
              }

              //to select first value for colour state
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (firstRecordForColourState == null || selectedRecord.getHigh() > firstRecordForColourState.getHigh())
                  firstRecordForColourState = selectedRecord;
                if (selectedRecord.getHighLowArrow().equals("Up") || selectedRecord.getHighLowArrow().equals("Down"))
                  break;
              }
              if (firstRecordForColourState != null)
                firstRangeForColourState = currentRecord.getTradedValue(firstRecordForColourState.getHigh(), State.UP, 0);

              //for select second value for colour state
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (selectedRecord.getHighLowArrow().equals("Down"))
                  secondRecordForColourState = selectedRecord;
                if (selectedRecord.getHighLowArrow().equals("Up") && secondRecordForColourState != null)
                  break;
              }
              if (secondRecordForColourState != null)
                secondRangeForColourState = currentRecord.getTradedValue(secondRecordForColourState.getLow(), State.UP, 0);

              currentRecord.calculateHighLowArrowColour(firstRangeForColourState, secondRangeForColourState, sameMoveCycleList);

              //to select first value for move type
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (firstRecordForMoveType == null || selectedRecord.getHigh() > firstRecordForMoveType.getHigh())
                  firstRecordForMoveType = selectedRecord;
                if (selectedRecord.getHighLowArrow().equals("Up") || selectedRecord.getHighLowArrow().equals("Down") || selectedRecord.getHighLowArrow().equals("N-Up") || selectedRecord.getHighLowArrow().equals("-NDown"))
                  break;
              }

              if (firstRecordForMoveType != null)
                firstRangeForMoveType = currentRecord.getTradedValue(firstRecordForMoveType.getHigh(), State.UP, 2);

              //to select second value for move type
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (selectedRecord.moveType == MoveType.STRONG_UP || selectedRecord.moveType == MoveType.STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) {
                  secondRecordForMoveType = selectedRecord;
                  break;
                }
              }

              if (secondRecordForMoveType != null)
                secondRangeForMoveType = currentRecord.getTradedValue(secondRecordForMoveType.getHigh(), State.UP, 2);

              currentRecord.calculateMoveType(firstRangeForMoveType, secondRangeForMoveType, firstRecordForMoveType, secondRecordForMoveType, State.UP, sameMoveCycleList);

            } else if (currentRecord.state == State.DOWN) {

              RecordCalculated firstRecordForColourState = null, firstRecordForMoveType = null, secondRecordForColourState = null, secondRecordForMoveType = null;
              Range firstRangeForMoveType = null, firstRangeForColourState = null, secondRangeForColourState = null, secondRangeForMoveType = null;

              List<RecordCalculated> sameMoveCycleList = new ArrayList<>();
              for (int j = i - 1; j >= 0; j--) {
                if (recordCalculatedList.get(j).state != currentRecord.state)
                  break;
                sameMoveCycleList.add(recordCalculatedList.get(j));
              }

              //to select first value for colour state
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (firstRecordForColourState == null || selectedRecord.getLow() < firstRecordForColourState.getLow())
                  firstRecordForColourState = selectedRecord;
                if (selectedRecord.getHighLowArrow().equals("Up") || selectedRecord.getHighLowArrow().equals("Down"))
                  break;
              }

              if (firstRecordForColourState != null)
                firstRangeForColourState = currentRecord.getTradedValue(firstRecordForColourState.getLow(), State.DOWN, 0);

              //to select second value for colour state
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (selectedRecord.getHighLowArrow().equals("Up"))
                  secondRecordForColourState = selectedRecord;
                if (selectedRecord.getHighLowArrow().equals("Down") && secondRecordForColourState != null)
                  break;
              }

              if (secondRecordForColourState != null)
                secondRangeForColourState = currentRecord.getTradedValue(secondRecordForColourState.getHigh(), State.DOWN, 0);

              currentRecord.calculateHighLowArrowColour(firstRangeForColourState, secondRangeForColourState, sameMoveCycleList);

              //to select first value for move type
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (firstRecordForMoveType == null || selectedRecord.getLow() < firstRecordForMoveType.getLow())
                  firstRecordForMoveType = selectedRecord;
                if (selectedRecord.getHighLowArrow().equals("Up") || selectedRecord.getHighLowArrow().equals("Down") || selectedRecord.getHighLowArrow().equals("N-Up") || selectedRecord.getHighLowArrow().equals("-NDown"))
                  break;
              }

              if (firstRecordForMoveType != null)
                firstRangeForMoveType = currentRecord.getTradedValue(firstRecordForMoveType.getLow(), State.DOWN, 2);

              //to select second value for move type
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (selectedRecord.moveType == MoveType.STRONG_UP || selectedRecord.moveType == MoveType.STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) {
                  secondRecordForMoveType = selectedRecord;
                  break;
                }
              }

              if (secondRecordForMoveType != null)
                secondRangeForMoveType = currentRecord.getTradedValue(secondRecordForMoveType.getLow(), State.DOWN, 2);

              currentRecord.calculateMoveType(firstRangeForMoveType, secondRangeForMoveType, firstRecordForMoveType, secondRecordForMoveType, State.DOWN, sameMoveCycleList);

            } else {
              currentRecord.calculateHighLowArrowColour(null, null, new ArrayList<>());
            }
          }
        }

        float selectedValue=0.0F;
        boolean isValueSelected=false;
        for (int i = 0; i < recordCalculatedList.size(); i++) {
          RecordCalculated recordCalculated = recordCalculatedList.get(i);
          if (i == 0) {
            if(recordCalculated.state==State.UP) {
              selectedValue = recordCalculated.getHigh();
              isValueSelected = true;
            }
            else if(recordCalculated.state == State.DOWN) {
              selectedValue = recordCalculated.getLow();
              isValueSelected = true;
            }
          } else {
            RecordCalculated previousRecord= recordCalculatedList.get(i-1);

            if(recordCalculated.state == previousRecord.state && isValueSelected){
              if (recordCalculated.state == State.UP && recordCalculated.getHigh() > selectedValue) {
                selectedValue = recordCalculated.getHigh();
              } else if (recordCalculated.state == State.DOWN && recordCalculated.getLow() < selectedValue) {
                selectedValue = recordCalculated.getLow();
              }
            } else {
              if(isValueSelected) {
                for (int j = i - 1; i >= 0; j--) {
                  RecordCalculated selectedRecord = recordCalculatedList.get(j);
                  if (previousRecord.state != selectedRecord.state)
                    break;
                  if (previousRecord.state == State.UP && selectedRecord.getHigh() == selectedValue) {
                    selectedRecord.setSelected(true);
                  } else if (previousRecord.state == State.DOWN && selectedRecord.getLow() == selectedValue) {
                    selectedRecord.setSelected(true);
                  }
                }
                isValueSelected=false;
              }

              if(recordCalculated.state==State.UP) {
                selectedValue = recordCalculated.getHigh();
                isValueSelected=true;
              }
              else if(recordCalculated.state == State.DOWN) {
                selectedValue = recordCalculated.getLow();
                isValueSelected = true;
              }
            }
          }
        }

        ScripWithRecordDTO scripWithRecordDTO = new ScripWithRecordDTO(scrip.getId(), scrip.getName(), scrip.getIsin(), scrip.getSeries(), recordCalculatedList);
        return ResponseEntity.ok(scripWithRecordDTO);
      } else {
        return ResponseEntity.badRequest().body(scanRequestDTO);
      }
    } catch (ParseException e) {
      return ResponseEntity.badRequest().body(scanRequestDTO);
    }

  }

  private float getSplitValue(float value,int numerator, int denominator){
    return Math.round(((value*denominator)/numerator)*100)/100F;
  }

  private float getSplitReverseValue(float value,int numerator, int denominator){
    return Math.round(((value*numerator)/denominator)*100)/100F;
  }

  private float getBonusValue(float value,int numerator, int denominator){
    return Math.round(((value*denominator)/(numerator+denominator))*100)/100F;
  }

  private float getBonusReverseValue(float value,int numerator, int denominator){
    return Math.round(((value*(numerator+denominator))/denominator)*100)/100F;
  }

  @RequestMapping(value = "price-adjustment", method = RequestMethod.POST)
  public ResponseEntity<?> adjustPrice(@RequestBody PriceAdjustmentDTO priceAdjustmentDTO){
    Map<String, String> responseData = new HashMap<>();
    try {
      Date date = new SimpleDateFormat("dd-MM-yyyy").parse(priceAdjustmentDTO.getDate());
      Optional<Scrip> optionalScrip = scripService.findById(priceAdjustmentDTO.getScripId());
      if(optionalScrip.isPresent()){
        Scrip scrip = optionalScrip.get();
        List<Record> records = recordService.findByScripIdAndDateLessThanEqual(scrip.getId(), date);
        if(priceAdjustmentDTO.getPriceAdjustmentType() == 1) {
          for (Record record : records) {
            record.setClose(getBonusValue(record.getClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setOpen(getBonusValue(record.getOpen(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setHigh(getBonusValue(record.getHigh(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLow(getBonusValue(record.getLow(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLast(getBonusValue(record.getLast(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setPreviousClose(getBonusValue(record.getPreviousClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            recordService.save(record);
          }
        }
        else if(priceAdjustmentDTO.getPriceAdjustmentType() == 2) {
          for (Record record : records) {
            record.setClose(getSplitValue(record.getClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setOpen(getSplitValue(record.getOpen(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setHigh(getSplitValue(record.getHigh(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLow(getSplitValue(record.getLow(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLast(getSplitValue(record.getLast(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setPreviousClose(getSplitValue(record.getPreviousClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            recordService.save(record);
          }
        }
        else if(priceAdjustmentDTO.getPriceAdjustmentType() == 3) {
          for (Record record : scrip.getRecords()) {
            record.setClose(getBonusReverseValue(record.getClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setOpen(getBonusReverseValue(record.getOpen(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setHigh(getBonusReverseValue(record.getHigh(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLow(getBonusReverseValue(record.getLow(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLast(getBonusReverseValue(record.getLast(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setPreviousClose(getBonusReverseValue(record.getPreviousClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            recordService.save(record);
          }
        }
        else if(priceAdjustmentDTO.getPriceAdjustmentType() == 4) {
          for (Record record : scrip.getRecords()) {
            record.setClose(getSplitReverseValue(record.getClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setOpen(getSplitReverseValue(record.getOpen(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setHigh(getSplitReverseValue(record.getHigh(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLow(getSplitReverseValue(record.getLow(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setLast(getSplitReverseValue(record.getLast(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            record.setPreviousClose(getSplitReverseValue(record.getPreviousClose(), priceAdjustmentDTO.getNumerator(), priceAdjustmentDTO.getDenominator()));
            recordService.save(record);
          }
        }
        responseData.put("message", "Price has been successfully updated");
        return ResponseEntity.ok().body(responseData);
      }else{
        responseData.put("message", "Scrip is not exist");
        return ResponseEntity.badRequest().body(responseData);
      }
    } catch (ParseException e) {
      e.printStackTrace();
      responseData.put("message", "Date is invalid");
      return ResponseEntity.badRequest().body(responseData);
    }
  }


}