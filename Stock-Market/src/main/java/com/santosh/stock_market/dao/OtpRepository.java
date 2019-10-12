package com.santosh.stock_market.dao;

import com.santosh.stock_market.model.Otp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends CrudRepository<Otp, Long> {

}
