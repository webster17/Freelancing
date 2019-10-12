package com.santosh.stock_market;

import com.santosh.stock_market.dao.Monitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StockMarketApplication {

  public static void main(String[] args) {
    SpringApplication.run(StockMarketApplication.class, args);
    System.out.println("Server is running...");
  }

  @Bean
  public Monitor monitor(){

  }
}