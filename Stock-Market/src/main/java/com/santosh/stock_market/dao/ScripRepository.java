package com.santosh.stock_market.dao;

import com.santosh.stock_market.model.Profile;
import com.santosh.stock_market.model.Scrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScripRepository extends JpaRepository<Scrip, Long> {

  List<Scrip> findByIdIn(@Param("ids") List<Long> ids);

  Optional<Scrip> findByIsin(@Param("isin") String isin);

//  @Query("SELECT s FROM Scrip s WHERE s.name = :name")
  List<Scrip> findByName(@Param("name") String name);

}