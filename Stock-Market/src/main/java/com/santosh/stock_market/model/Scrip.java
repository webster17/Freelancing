package com.santosh.stock_market.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scrips")
public class Scrip {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  @Column(name = "scrip_id")
  private long id;

  @Column(unique = true, nullable = false)
  private String name;

  private String description;

  @ManyToMany(mappedBy = "scrips", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
  private Set<Profile> profiles = new HashSet<>();

  public Scrip(){}

  public Scrip(long id, String name, String description){
    super();
    this.id = id;
    this.name = name;
    this.description = description;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<Profile> getUsers() {
    return profiles;
  }

  public void addUser(Profile profile) {
    profiles.add(profile);
    profile.getScrips().add(this);
  }

  @Override
  public String toString() {
    return "Scrip{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }

}
