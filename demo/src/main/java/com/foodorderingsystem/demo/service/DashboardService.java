package com.foodorderingsystem.demo.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DashboardService {
    public ResponseEntity<Map<String, Object>> getCount();
}
