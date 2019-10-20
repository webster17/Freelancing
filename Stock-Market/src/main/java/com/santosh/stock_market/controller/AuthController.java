package com.santosh.stock_market.controller;

import com.santosh.stock_market.model.Otp;
import com.santosh.stock_market.model.User;
import com.santosh.stock_market.utility.SimpleOTPGenerator;
import com.santosh.stock_market.utility.config.jwt.JWTTokenUtil;
import com.santosh.stock_market.dto.JWTRequest;
import com.santosh.stock_market.dto.JWTResponse;
import com.santosh.stock_market.dto.UserDTO;
import com.santosh.stock_market.service.JWTUserDetailsService;
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
        User user = userDetailsService.getUserByEmail(authenticationRequest.getEmail());
        return ResponseEntity.ok(new JWTResponse(user.getId(), user.getEmail(), user.getName(), user.getAdmin(), token));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO userDTO) throws Exception {
        return ResponseEntity.ok(userDetailsService.save(userDTO));
    }

    @RequestMapping(value = "/sendOtp", method = RequestMethod.POST)
    public ResponseEntity<?> sendOtp(@RequestBody UserDTO userDTO) throws Exception {
        User user = userDetailsService.getUserByEmail(userDTO.getEmail());
        if(user!=null){
            Otp otp = new Otp(SimpleOTPGenerator.random(6));
            user.setOtp(otp);
            otp.setUser(user);
            userDetailsService.save(user);
            return ResponseEntity.ok(otp);
        }else{
            Map<String, Object> responseData  = new HashMap<>();
            responseData.put("data", user);
            responseData.put("MSG","Not Found Data");
            return ResponseEntity.badRequest().body(responseData);
        }
    }


//    "auth/search", "auth/forgotPassword"


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
    public ResponseEntity<?> test(){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new JWTResponse(user.getId(), user.getEmail(), user.getName(), user.getAdmin(), ""));
    }



//    @PostMapping("login")
//    public String test() {
//        return "Welcome to Spring Boot 2";
//    }

//    @GetMapping("test2")
//    public User test2() {
//        User user = new User();
//        user.setName("ssssss");
//        return user;
//    }

}