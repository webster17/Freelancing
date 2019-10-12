package com.santosh.stock_market.dto;

import java.io.Serializable;

public class Response implements Serializable {

  private static final long serialVersionUID = -8091879091924046844L;
  private final Boolean hasError;
  private final Response response;


  public Response(Boolean hasError, Response response) {
    this.hasError = hasError;
    this.response=response;
  }

  public Boolean getHasError() {
    return hasError;
  }

}

