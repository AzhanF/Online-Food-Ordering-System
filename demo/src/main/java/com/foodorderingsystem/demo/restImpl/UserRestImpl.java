package com.foodorderingsystem.demo.restImpl;

import com.foodorderingsystem.demo.constents.FoodConstants;
import com.foodorderingsystem.demo.rest.UserRest;
import com.foodorderingsystem.demo.service.UserService;
import com.foodorderingsystem.demo.utils.FoodUtils;
import com.foodorderingsystem.demo.wrapper.UserWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserRestImpl implements UserRest {
    private final UserService userService;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signup(requestMap);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try{
            return userService.login(requestMap);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            return userService.update(requestMap);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        try {
            return userService.checkToken();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            return userService.changePassword(requestMap);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            return userService.forgotPassword(requestMap);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
