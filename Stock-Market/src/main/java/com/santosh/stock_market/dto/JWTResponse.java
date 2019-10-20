package com.santosh.stock_market.dto;

import java.io.Serializable;

public class JWTResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String userName;
    private final Boolean isAdmin;
    private final Long id;
    private final String email;
    private final String jwtToken;

    public JWTResponse(Long id, String email, String userName, Boolean isAdmin, String jwtToken) {
        this.id=id;
        this.email=email;
        this.userName=userName;
        this.isAdmin=isAdmin;
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return this.jwtToken;
    }

    public String getUserName() {
        return userName;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}