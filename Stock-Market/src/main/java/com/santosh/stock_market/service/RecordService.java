package com.santosh.stock_market.service;

import com.santosh.stock_market.dao.RecordRepository;
import com.santosh.stock_market.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RecordService {

  @Autowired
  RecordRepository recordRepository;

  public void save(Record rawRecord){
    recordRepository.save(rawRecord);
  }

  public void deleteRecordByDate(Date date){
    recordRepository.deleteRecordByDate(date);
  }

  public List<Record> findByScripIdAndStartDateBeforeAndEndDateAfter(Long id, Date startDate, Date endDate){
    return recordRepository.findByScripIdAndDateBetween(id, startDate, endDate, new Sort(Sort.Direction.ASC, "date"));
  }

}