package com.santosh.stock_market.service;

import java.util.ArrayList;
import java.util.Optional;

import com.santosh.stock_market.dao.Monitor;
import com.santosh.stock_market.dao.UserRepository;
import com.santosh.stock_market.model.User;
import com.santosh.stock_market.dto.UserDTO;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Service
public class JWTUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bCryptEncoder;
//
    @Autowired
    private Monitor monitor;
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    public void context(ApplicationContext context) { this.context = context; }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> optional = userRepository.findByEmail(email);

        if(optional.isPresent()){
            User user = optional.get();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new ArrayList<>());
        }
        else {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
    }

    public User getUserByEmail(String email){
        Optional<User> optional = userRepository.findByEmail(email);

        if (optional.isPresent()){
            return optional.get();
        }
        return null;
//        else {
//            throw new UsernameNotFoundException("User not found with username: " + email);
//        }
    }

    public void save(User user){
        userRepository.save(user);
    }

    @Modifying
    public User save(UserDTO userDTO) {
        User newUser = new User();
        System.out.println(monitor.getUsers());
//            User newUser = new User();
            newUser.setEmail(userDTO.getEmail());
            newUser.setAdmin(userDTO.getAdmin());
            newUser.setName(userDTO.getName());
            newUser.setPassword(bCryptEncoder.encode(userDTO.getPassword()));

            
        return newUser;
    }

}