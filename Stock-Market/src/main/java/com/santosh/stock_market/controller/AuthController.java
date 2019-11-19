package com.santosh.stock_market.controller;

import com.santosh.stock_market.dto.ChangePasswordDto;
import com.santosh.stock_market.dto.JWTRequest;
import com.santosh.stock_market.dto.JWTResponse;
import com.santosh.stock_market.dto.UserDTO;
import com.santosh.stock_market.model.Otp;
import com.santosh.stock_market.model.User;
import com.santosh.stock_market.service.JWTUserDetailsService;
import com.santosh.stock_market.utility.SimpleOTPGenerator;
import com.santosh.stock_market.utility.config.jwt.JWTTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JWTTokenUtil jwtTokenUtil;

  @Autowired
  private JWTUserDetailsService userDetailsService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody JWTRequest authenticationRequest) throws Exception {
    authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
    final UserDetails userDetails = userDetailsService
        .loadUserByUsername(authenticationRequest.getEmail());
    final String token = jwtTokenUtil.generateToken(userDetails);
    User user = userDetailsService.findByEmail(authenticationRequest.getEmail());
    return ResponseEntity.ok(new JWTResponse(user.getId(), user.getEmail(), user.getName(), user.getAdmin(), token));
  }

  @RequestMapping(value = "/sendOTP", method = RequestMethod.POST)
  public ResponseEntity<?> sendOtp(@RequestBody UserDTO userDTO) throws Exception {
    User user = userDetailsService.findByEmail(userDTO.getEmail());
//        return ResponseEntity.ok(user);
    if (user != null) {

      if (user.getOtp() != null) {
        user.getOtp().setOtp(SimpleOTPGenerator.random(6));
      } else {
        Otp otp = new Otp(SimpleOTPGenerator.random(6));
        user.setOtp(otp);
        otp.setUser(user);
      }
      userDetailsService.save(user);
      return ResponseEntity.ok(user.getOtp());
    } else {
      Map<String, String> responseData = new HashMap<>();
      responseData.put("message", "user not found");
      return ResponseEntity.badRequest().body(responseData);
    }
  }

  @RequestMapping(value = "/setNewPassword", method = RequestMethod.POST)
  public ResponseEntity<?> setNewPassword(@RequestBody UserDTO userDTO) throws Exception {
    User user = userDetailsService.findByEmail(userDTO.getEmail());
    if (user != null) {
      if (user.getOtp() != null) {
        if (userDTO.getOtp().equals(user.getOtp().getOtp())) {
          userDetailsService.updatePassword(user, userDTO.getPassword());
          Map<String, String> responseData = new HashMap<>();
          responseData.put("message", "New password has been successfully updated");
          return ResponseEntity.ok().body(responseData);
        } else {
          Map<String, String> responseData = new HashMap<>();
          responseData.put("message", "Enter valid otp");
          return ResponseEntity.badRequest().body(responseData);
        }
      } else {
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "Enter valid otp");
        return ResponseEntity.badRequest().body(responseData);
      }
    } else {
      Map<String, String> responseData = new HashMap<>();
      responseData.put("message", "user not found");
      return ResponseEntity.badRequest().body(responseData);
    }
  }

  @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
  public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto) throws Exception {

    User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userDetailsService.findByEmail(authUser.getEmail());

    authenticate(authUser.getEmail(), changePasswordDto.getCurrentPassword());

    userDetailsService.updatePassword(user, changePasswordDto.getNewPassword());
    Map<String, String> responseData = new HashMap<>();
    responseData.put("message", "New password has been successfully updated");
    return ResponseEntity.ok().body(responseData);
  }

  private void authenticate(String username, String password) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }

  @GetMapping("getAuthenticated")
  public ResponseEntity<?> test() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ResponseEntity.ok(new JWTResponse(user.getId(), user.getEmail(), user.getName(), user.getAdmin(), ""));
  }

}