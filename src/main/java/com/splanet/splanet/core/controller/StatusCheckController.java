package com.splanet.splanet.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusCheckController {

  @GetMapping("/actuator/health-check")
  public ResponseEntity<Void> checkHealthStatus() {
    return new ResponseEntity<>(HttpStatus.OK);
  }
}