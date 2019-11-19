package com.santosh.stock_market.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "otp")
public class Otp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String otp;

  public Otp(){}

  public Otp(String otp){
    this.otp=otp;
  }

  @OneToOne
  @MapsId
  @JsonIgnore
  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }

}