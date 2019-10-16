package com.santosh.stock_market.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.santosh.stock_market.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>{

}
