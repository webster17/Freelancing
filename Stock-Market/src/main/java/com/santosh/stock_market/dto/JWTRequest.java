package com.santosh.stock_market.dto;

import java.io.Serializable;

public class JWTRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;
    private String email;
    private String password;

    //need default constructor for JSON Parsing
    public JWTRequest() {
    }

    public JWTRequest(String username, String password) {
        this.setEmail(username);
        this.setPassword(password);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}