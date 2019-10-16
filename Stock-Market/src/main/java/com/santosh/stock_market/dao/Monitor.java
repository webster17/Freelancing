package com.santosh.stock_market.dao;

import com.santosh.stock_market.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Monitor {

  private UserRepository userRepository;
  private List<User> users;
  
  @Autowired
  public Monitor(UserRepository userRepository){
    System.out.println("Application Started ");
    this.userRepository = userRepository;
    users = userRepository.findAll();
    System.out.print("Application done "+users.toString());
  }

  public List<User> getUsers() {
    return users;
  }
}
