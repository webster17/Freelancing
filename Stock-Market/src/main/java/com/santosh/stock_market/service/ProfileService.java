package com.santosh.stock_market.service;

import com.santosh.stock_market.dao.ProfileRepository;
import com.santosh.stock_market.dto.ProfileDTO;
import com.santosh.stock_market.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

  @Autowired
  private ProfileRepository profileRepository;

  public void save(Profile profile) {
    profileRepository.save(profile);
  }

  public Optional<Profile> findByName(String name) {
    return profileRepository.findByName(name);
  }

  public List<ProfileDTO> getAllSorted(){
    return profileRepository.findAllSorted();
  }

  public Optional<Profile> getProfileById(Long id){
    return profileRepository.findById(id);
  }

  public void deleteProfileById(Long id){
    profileRepository.deleteById(id);
  }

  public void delete(Profile profile){
    profileRepository.delete(profile);
  }

}
