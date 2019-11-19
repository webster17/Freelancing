package com.santosh.stock_market.service;

import com.santosh.stock_market.dao.UserRepository;
import com.santosh.stock_market.dto.UserDTO;
import com.santosh.stock_market.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JWTUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder bCryptEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Optional<User> optional = userRepository.findByEmail(email);

    if (optional.isPresent()) {
      User user = optional.get();
      return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
          new ArrayList<>());
    } else {
      throw new UsernameNotFoundException("User not found with username: " + email);
    }
  }

  public User findByEmail(String email) {
    Optional<User> optional = userRepository.findByEmail(email);
    return optional.orElse(null);
  }

  public User findById(Long id) {
    Optional<User> optional = userRepository.findById(id);
    return optional.orElse(null);
  }

  public List<User> findAll() {
    return userRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
  }

  public void save(User user) {
    userRepository.save(user);
  }

  public User save(UserDTO userDTO) {
    User newUser = new User();
    newUser.setEmail(userDTO.getEmail());
    newUser.setAdmin(userDTO.getAdmin());
    newUser.setName(userDTO.getName());
    newUser.setPassword(bCryptEncoder.encode(userDTO.getPassword()));
    return userRepository.save(newUser);
  }

  public void delete(User user) {
    userRepository.delete(user);
  }

  public User update(User user, UserDTO userDTO) {
    user.setEmail(userDTO.getEmail());
    user.setAdmin(userDTO.getAdmin());
    user.setName(userDTO.getName());
    user.setPassword(bCryptEncoder.encode(userDTO.getPassword()));
    return userRepository.save(user);
  }

  public void updatePassword(User user, String newPassword) {
    user.setPassword(bCryptEncoder.encode(newPassword));
    userRepository.save(user);
  }

}