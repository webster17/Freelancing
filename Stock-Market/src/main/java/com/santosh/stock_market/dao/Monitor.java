package com.santosh.stock_market.dao;

import com.santosh.stock_market.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Monitor {

  @Autowired
  private UserRepository userRepository;
  private List<User> users;
  @Autowired
  public Monitor(){
    System.out.println("Application Started ");
    users = userRepository.findAll();

  }

  public List<User> getUsers() {
    return users;
  }
}
