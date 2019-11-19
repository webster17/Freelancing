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
        List<Record> records = recordService.findByScripIdAndStartDateBeforeAndEndDateAfter(scripId, startDate, endDate);
        List<RecordCalculated> recordCalculatedList = new ArrayList<>();
        for (Record record : records) {
          recordCalculatedList.add(new RecordCalculated(record.getId(), record.getScrip().getName(), record.getHigh(), record.getLow(), record.getOpen(), record.getClose(), record.getLast(), record.getPreviousClose(), record.getTotalTradeQuantity(), record.getTotalTrades(), record.getTotalTradeValue(), record.getDate()));
        }

        if (recordCalculatedList.size() > 0) {
          recordCalculatedList.get(0).calculateHighLowArrow(null, null);
          recordCalculatedList.get(0).calculateHighLowArrowColour(null, "grey");

          for (int i = 1; i < recordCalculatedList.size(); i++) {

            RecordCalculated currentRecord = recordCalculatedList.get(i);
            RecordCalculated previousRecord = recordCalculatedList.get(i - 1);

            RecordCalculated mostRecentUpOrDownRecord = null;
            for (int k = i-1; k >= 0; k--) {
              RecordCalculated recordCalculated = recordCalculatedList.get(k);
              System.out.println("J:" +k+"  "+recordCalculated.getHighLowArrow());
              if (recordCalculatedList.get(k).getHighLowArrow().equals("Up") || recordCalculatedList.get(k).getHighLowArrow().equals("Down")) {
                mostRecentUpOrDownRecord = recordCalculatedList.get(k);
                break;
              }
            }

            currentRecord.calculateHighLowArrow(previousRecord, mostRecentUpOrDownRecord);
            currentRecord.setPreviousRange(previousRecord.getRange());

            if (currentRecord.state == State.UP) {

              //for strong weak calculation
              if (currentRecord.getHighLowArrow().equals("Up")) {
                RecordCalculated firstRecord = null, secondRecord = null;
                //to select first value
                for (int j = i - 1; j >= 0; j--) {
                  RecordCalculated selectedRecord = recordCalculatedList.get(j);
                  if (firstRecord == null || selectedRecord.getHigh() > firstRecord.getHigh())
                    firstRecord = selectedRecord;
                  if (selectedRecord.getHighLowArrow().equals("Up") || selectedRecord.getHighLowArrow().equals("Down"))
                    break;
                }

                //to select second value
                for (int j = i - 1; j >= 0; j--) {
                  RecordCalculated selectedRecord = recordCalculatedList.get(j);
                  if (selectedRecord.moveType == MoveType.STRONG_UP || selectedRecord.moveType == MoveType.STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) {
                    secondRecord = selectedRecord;
                    break;
                  }
                }

                Range firstRange = null, secondRange = null;
                if (firstRecord != null)
                  firstRange = currentRecord.getTradedValue(firstRecord.getHigh(), State.UP, 2);
                if (secondRecord != null)
                  secondRange = currentRecord.getTradedValue(secondRecord.getHigh(), State.UP, 2);

                List<RecordCalculated> sameMoveCycleList = new ArrayList<>();
                for (int j = i - 1; j >= 0; j--) {
                  if (recordCalculatedList.get(j).state != currentRecord.state)
                    break;
                  sameMoveCycleList.add(recordCalculatedList.get(j));
                }

                currentRecord.calculateMoveType(firstRange, secondRange, firstRecord, secondRecord, State.UP, sameMoveCycleList);

              } else {
                currentRecord.setMoveType(MoveType.BLANK);
              }

              //for colour purpose
              int firstDownEncountered = -1;
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (selectedRecord.getHighLowArrow().equals("Down")) {
                  firstDownEncountered = j;
                }
                if (selectedRecord.getHighLowArrow().equals("Up")) {
                  break;
                }
              }
              if (firstDownEncountered != -1) {
                currentRecord.calculateHighLowArrowColour(currentRecord.getTradedValue(recordCalculatedList.get(firstDownEncountered).getHigh(), State.UP, 0), previousRecord.getHighLowArrowColour());
              } else {
                currentRecord.calculateHighLowArrowColour(null, previousRecord.getHighLowArrowColour());
              }

//              if()
            } else if (currentRecord.state == State.DOWN) {

              //for strong weak calculation
              if (currentRecord.getHighLowArrow().equals("DOWN")) {

                RecordCalculated firstRecord = null, secondRecord = null;

                //to select first value
                for (int j = i - 1; j >= 0; j--) {
                  RecordCalculated selectedRecord = recordCalculatedList.get(j);
                  if (firstRecord == null || selectedRecord.getLow() < firstRecord.getLow())
                    firstRecord = selectedRecord;
                  if (selectedRecord.getHighLowArrow().equals("Up") || selectedRecord.getHighLowArrow().equals("Down"))
                    break;
                }

                //to select second value
                for (int j = i - 1; j >= 0; j--) {
                  RecordCalculated selectedRecord = recordCalculatedList.get(j);
                  if (selectedRecord.moveType == MoveType.STRONG_UP || selectedRecord.moveType == MoveType.STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_DOWN || selectedRecord.moveType == MoveType.JUSTIFIED_STRONG_UP) {
                    secondRecord = selectedRecord;
                    break;
                  }
                }

                Range firstRange = null, secondRange = null;
                if (firstRecord != null)
                  firstRange = currentRecord.getTradedValue(firstRecord.getLow(), State.DOWN, 2);
                if (secondRecord != null)
                  secondRange = currentRecord.getTradedValue(secondRecord.getLow(), State.DOWN, 2);

                List<RecordCalculated> sameMoveCycleList = new ArrayList<>();
                for (int j = i - 1; j >= 0; j--) {
                  if (recordCalculatedList.get(j).state != currentRecord.state)
                    break;
                  sameMoveCycleList.add(recordCalculatedList.get(j));
                }

                currentRecord.calculateMoveType(firstRange, secondRange, firstRecord, secondRecord, State.DOWN, sameMoveCycleList);

              } else {
                currentRecord.setMoveType(MoveType.BLANK);
              }

              //for colour purpose
              int firstUpEncountered = -1;
              for (int j = i - 1; j >= 0; j--) {
                RecordCalculated selectedRecord = recordCalculatedList.get(j);
                if (selectedRecord.getHighLowArrow().equals("Up")) {
                  firstUpEncountered = j;
                }
                if (selectedRecord.getHighLowArrow().equals("Down")) {
                  break;
                }
              }
              if (firstUpEncountered != -1) {
                currentRecord.calculateHighLowArrowColour(currentRecord.getTradedValue(recordCalculatedList.get(firstUpEncountered).getLow(), State.DOWN, 0), previousRecord.getHighLowArrowColour());
              } else {
                currentRecord.calculateHighLowArrowColour(null, previousRecord.getHighLowArrowColour());
              }
            } else {
              currentRecord.calculateHighLowArrowColour(null, previousRecord.getHighLowArrowColour());
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

}