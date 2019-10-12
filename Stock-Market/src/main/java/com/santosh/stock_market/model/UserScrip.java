//package com.santosh.stock_market.entity;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "user_scrips")
//public class UserScripEntity {
//
//  @Id
//  @GeneratedValue(strategy= GenerationType.IDENTITY)
//  private Long id;
//
//  @Column(name="user_id")
//  private Long userId;
//
//  @Column(name="scrip_id")
//  private Long scripId;
//
//  public UserScripEntity(){}
//
//  public UserScripEntity(Long id, Long userId, Long scripId){
//    super();
//    this.id = id;
//    this.userId = userId;
//    this.scripId = scripId;
//  }
//
//  public long getId() {
//    return id;
//  }
//
//  public void setId(long id) {
//    this.id = id;
//  }
//
//  public Long getUserId() {
//    return userId;
//  }
//
//  public void setUserId(Long userId) {
//    this.userId = userId;
//  }
//
//  public Long getScripId() {
//    return scripId;
//  }
//
//  public void setScripId(Long scripId) {
//    this.scripId = scripId;
//  }
//
//}