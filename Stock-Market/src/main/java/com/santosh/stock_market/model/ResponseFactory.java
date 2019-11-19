package com.santosh.stock_market.model;

import java.util.Optional;

public class ResponseFactory {
	
	private int errorStatusCode;
	private String message;
	private Optional<Object> data;
	
	public int getErrorStatusCode() {
		return errorStatusCode;
	}
	public void setErrorStatusCode(int errorStatusCode) {
		this.errorStatusCode = errorStatusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Optional<Object> getData() {
		return data;
	}
	public void setData(Optional<Object> data) {
		this.data = data;
	}
	public ResponseFactory(int errorStatusCode, String message, Optional<Object> data) {
		this.errorStatusCode = errorStatusCode;
		this.message = message;
		this.data = data;
	}
}
