package com.foodorderingsystem.demo.service;

import com.foodorderingsystem.demo.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    public ResponseEntity<String> addCategory(Map<String, String> requestMap);

    public ResponseEntity<List<Category>> getCategories(String filterValue);
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap);
}
