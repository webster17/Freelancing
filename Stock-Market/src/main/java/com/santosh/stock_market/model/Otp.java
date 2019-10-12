package com.santosh.stock_market.model;

import javax.persistence.*;

@Entity
@Table(name = "otp")
public class Otp {

//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;

  @Id
  @Column(name="id")
  private Long id;

//  @OneToOne(fetch = FetchType.LAZY, optional = true)
//  @JoinColumn(name = "user_id", nullable = true)
//  private User user;

  private String otp;

  public Otp(){}

  public Otp(String otp){
//    this.id=id;
    this.otp=otp;
  }

  @OneToOne
  @MapsId
  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }

}