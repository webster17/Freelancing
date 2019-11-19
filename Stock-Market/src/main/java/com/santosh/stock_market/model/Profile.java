package com.santosh.stock_market.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "profiles")
public class Profile implements Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @Column(unique = true, nullable = false)
  private String name;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
  @JoinTable(name="profile_scrips", joinColumns={@JoinColumn(referencedColumnName="id")}
      , inverseJoinColumns={@JoinColumn(referencedColumnName="id")})
  private Set<Scrip> scrips=new HashSet<>();

  public Profile(String name, Set<Scrip> scrips){
    this.name = name;
    this.scrips =scrips;
  }

  public Profile() {
    super();
  }

  public Set<Scrip> getScrips() {
    return scrips;
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

  public void setScrips(Set<Scrip> scrips) {
    this.scrips = scrips;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", Scrips='" + scrips.stream().map(Scrip::getName).collect(Collectors.toList()) + '\'' +
        '}';
  }

}