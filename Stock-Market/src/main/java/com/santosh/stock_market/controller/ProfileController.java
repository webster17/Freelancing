package com.santosh.stock_market.controller;

import com.santosh.stock_market.dto.AddProfileDTO;
import com.santosh.stock_market.dto.RawRecordDTO;
import com.santosh.stock_market.model.Profile;
import com.santosh.stock_market.service.ProfileService;
import com.santosh.stock_market.service.ScripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {

  @Autowired
  private ProfileService profileService;

  @Autowired
  private ScripService scripService;

  @RequestMapping(value = "", method = RequestMethod.POST)
  public ResponseEntity<?> addProfile(@RequestBody AddProfileDTO addProfileDTO) throws Exception {
    Map<String, String> responseData = new HashMap<>();
    Optional<Profile> profile = profileService.findByName(addProfileDTO.getName());
    if (profile.isPresent()) {
      responseData.put("message", "Profile name is already exist!");
      return ResponseEntity.status(409).body(responseData);
    } else {
      profileService.save(new Profile(addProfileDTO.getName(), new HashSet<>(scripService.findByIdIn(addProfileDTO.getScripIds()))));
      responseData.put("message", "Profile has been successfully added");
      return ResponseEntity.ok().body(responseData);
    }
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<?> getAllSorted() throws Exception {
    return ResponseEntity.ok().body(profileService.getAllSorted());
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> getByIds(@PathVariable @NotNull Long id) {
    return ResponseEntity.ok().body(profileService.getProfileById(id));
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> findByIds(@PathVariable @NotNull Long id, @RequestBody AddProfileDTO addProfileDTO) {
    Map<String, String> responseData = new HashMap<>();

    Optional<Profile> profile = profileService.getProfileById(id);
    if(profile.isPresent()) {
      Optional<Profile> profileWithSameName = profileService.findByName(addProfileDTO.getName());
      if ((profileWithSameName.isPresent() && profileWithSameName.get().getId() == id) || profileWithSameName.isEmpty()) {
        Profile updateProfile = profile.get();
        updateProfile.setName(addProfileDTO.getName());
        updateProfile.setScrips(new HashSet<>(scripService.findByIdIn(addProfileDTO.getScripIds())));
        profileService.save(updateProfile);
        responseData.put("message", "Profile has been successfully updated");
        return ResponseEntity.ok().body(responseData);
      } else {
        responseData.put("message", "Profile name is already exist!");
        return ResponseEntity.status(409).body(responseData);
      }
    }else{
      responseData.put("message", "Profile is not exist!");
      return ResponseEntity.status(204).body(responseData);
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteById(@PathVariable @NotNull Long id) {
    Map<String, String> responseData = new HashMap<>();

    Optional<Profile> profile = profileService.getProfileById(id);
    if (profile.isPresent()) {
      Profile profileModel = profile.get();
      profileModel.setScrips(null);
      profileService.save(profileModel);
      profileService.delete(profileModel);
      responseData.put("message", profileModel.getName() + " has been successfully deleted!");
      return ResponseEntity.ok().body(responseData);
    } else {
      responseData.put("message", "Profile is not exist!");
      return ResponseEntity.status(204).body(responseData);
    }
  }

}