package com.santosh.stock_market.dao;

import com.santosh.stock_market.model.Record;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

  @Transactional
  @Modifying
  @Query("DELETE FROM Record r WHERE r.date = :date")
  int deleteRecordByDate(@Param("date") Date date);

  List<Record> findByScripIdAndDateBetween(Long scripId, Date startDate, Date endDate, Sort sort);

}
