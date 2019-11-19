package com.santosh.stock_market.dao;

import com.santosh.stock_market.dto.ProfileDTO;
import com.santosh.stock_market.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  Optional<Profile> findByName(@Param("name") String name);

  @Query("SELECT new com.santosh.stock_market.dto.ProfileDTO(p.id, p.name) from Profile p ORDER BY p.name ASC")
  List<ProfileDTO> findAllSorted();

//	List<AddProfileDTO> getAllProfile();
//
//
//
//	@Query("SELECT s FROM Scrip s WHERE s.isin = :isin")
//	Optional<Scrip> findByIsin(@Param("isin") String isin);
//
//	@Query("SELECT s FROM Scrip s WHERE s.name = :name")
//	List<Scrip> findByName(@Param("name") String name);

}