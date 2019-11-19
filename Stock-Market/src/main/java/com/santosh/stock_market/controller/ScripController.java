package com.santosh.stock_market.controller;

import com.santosh.stock_market.service.ScripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/scrip")
public class ScripController {

  @Autowired
  private ScripService scripService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public @ResponseBody
  ResponseEntity<?> getAllSorted(){
    return ResponseEntity.ok().body(scripService.getAllScripSorted());
  }

  @RequestMapping(value = "/{isin}", method = RequestMethod.GET)
  public @ResponseBody
  ResponseEntity<?> getByIsin(@PathVariable String isin){
    return ResponseEntity.ok().body(scripService.findByIsin(isin));
  }

  @RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
  public @ResponseBody
  ResponseEntity<?> getByName(@PathVariable String name){
    return ResponseEntity.ok().body(scripService.findByName(name));
  }




}
