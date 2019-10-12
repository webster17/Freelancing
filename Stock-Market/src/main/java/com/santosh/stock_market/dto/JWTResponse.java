package com.santosh.stock_market.dto;

import java.io.Serializable;

public class JWTResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String userName;
    private final Boolean isAdmin;
    private final String jwtToken;

    public JWTResponse(String userName, Boolean isAdmin, String jwtToken) {
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

}