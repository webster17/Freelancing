package com.santosh.stock_market.controller;

import com.santosh.stock_market.dto.UserDTO;
import com.santosh.stock_market.model.User;
import com.santosh.stock_market.service.JWTUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

  @Autowired
  private JWTUserDetailsService userDetailsService;

  @RequestMapping(value = "", method = RequestMethod.POST)
  public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) throws Exception {
    Map<String, String> responseData = new HashMap<>();
    User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (authUser.getAdmin()) {
      User user = userDetailsService.findByEmail(userDTO.getEmail());
      if (user == null) {
        return ResponseEntity.ok(userDetailsService.save(userDTO));
      } else {
        responseData.put("message", "User already exist with same email");
        return ResponseEntity.status(409).body(responseData);
      }
    } else {
      responseData.put("message", "You are not authorized!");
      return ResponseEntity.status(401).body(responseData);
    }
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<?> getAllUser() throws Exception {
    return ResponseEntity.ok(userDetailsService.findAll());
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> getByUserId(@PathVariable Long id){
    return ResponseEntity.ok().body(userDetailsService.findById(id));
  }

  public ResponseEntity<?> getUser() throws Exception {
    return ResponseEntity.ok(userDetailsService.findAll());
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateUser(@PathVariable @NotNull Long id, @RequestBody UserDTO userDTO) {
    Map<String, String> responseData = new HashMap<>();
    User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (authUser.getAdmin()) {
      if (authUser.getId() != id) {
        User user = userDetailsService.findById(id);
        if (user != null) {
          User userWithSameEmail = userDetailsService.findByEmail(userDTO.getEmail());
          if (userWithSameEmail == null || userWithSameEmail.getId() == user.getId()) {
            return ResponseEntity.ok().body(userDetailsService.update(user, userDTO));
          } else {
            responseData.put("message", "Email is already exist!");
            return ResponseEntity.status(409).body(responseData);
          }
        } else {
          responseData.put("message", "User is not exist!");
          return ResponseEntity.status(204).body(responseData);
        }
      } else {
        responseData.put("message", "You can not update own profile!");
        return ResponseEntity.status(403).body(responseData);
      }
    } else {
      responseData.put("message", "You are not authorized!");
      return ResponseEntity.status(401).body(responseData);
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteUser(@PathVariable @NotNull Long id) {
    Map<String, String> responseData = new HashMap<>();
    User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (authUser.getAdmin()) {
      if (authUser.getId() != id) {
        User user = userDetailsService.findById(id);
        if (user != null) {
          user.setOtp(null);
          userDetailsService.save(user);
          userDetailsService.delete(user);
          responseData.put("message", user.getName() + " has been successfully deleted!");
          return ResponseEntity.ok().body(responseData);
        } else {
          responseData.put("message", "User is not exist!");
          return ResponseEntity.status(204).body(responseData);
        }
      } else {
        responseData.put("message", "You can not delete own profile!");
        return ResponseEntity.status(403).body(responseData);
      }
    } else {
      responseData.put("message", "You are not authorized!");
      return ResponseEntity.status(401).body(responseData);
    }
  }

}
