package com.santosh.stock_market.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scrips")
public class Scrip {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @Column(unique = true, nullable = false, length = 15)
  private String isin;

  @Column(nullable = false, length = 25)
  private String name;

  @Column(length = 3)
  private String series;

  @ManyToMany(mappedBy = "scrips", cascade = CascadeType.ALL)
  @JsonIgnore
  private Set<Profile> profiles = new HashSet<>();

  @OneToMany(mappedBy = "scrip", cascade = CascadeType.ALL)
  @JsonIgnore
  @OrderBy("date DESC")
  private Set<Record> records = new HashSet<>();

  public Scrip(){}

  public Scrip(String name, String isin, String series){
    super();
    this.name = name;
    this.isin = isin;
    this.series = series;
  }

  public void setRecords(Set<Record> records) {
    this.records = records;
  }

  public void setProfiles(Set<Profile> profiles){
    this.profiles=profiles;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Profile> getProfiles() {
    return profiles;
  }

  public void addProfile(Profile profile) {
    profiles.add(profile);
    profile.getScrips().add(this);
  }

  public String getIsin() {
    return isin;
  }

  public void setIsin(String isin) {
    this.isin = isin;
  }

  public String getSeries() {
    return series;
  }

  public Set<Record> getRecords() {
    return records;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  @Override
  public String toString() {
    return "Scrip{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }

}