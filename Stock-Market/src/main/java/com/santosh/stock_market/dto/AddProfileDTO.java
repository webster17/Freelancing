package com.santosh.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.santosh.stock_market.model.Profile;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties
public class AddProfileDTO implements Serializable {
	
	private long id;
	private String name;
	private List<Long> scripIds;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getScripIds() {
		return scripIds;
	}

	public void setScripIds(List<Long> scripIds) {
		this.scripIds = scripIds;
	}

}