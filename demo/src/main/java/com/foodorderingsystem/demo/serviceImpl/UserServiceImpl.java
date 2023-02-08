package com.foodorderingsystem.demo.serviceImpl;

import com.google.common.base.Strings;
import com.foodorderingsystem.demo.constents.FoodConstants;
import com.foodorderingsystem.demo.dao.UserDao;
import com.foodorderingsystem.demo.JWT.CustomerUsersDetailsService;
import com.foodorderingsystem.demo.JWT.JwtFilter;
import com.foodorderingsystem.demo.JWT.JwtUtil;
import com.foodorderingsystem.demo.POJO.User;
import com.foodorderingsystem.demo.service.UserService;
import com.foodorderingsystem.demo.utils.FoodUtils;
import com.foodorderingsystem.demo.utils.EmailUtils;
import com.foodorderingsystem.demo.wrapper.UserWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final AuthenticationManager authenticationManager;
    private final CustomerUsersDetailsService customerUsersDetailsService;
    private final JwtUtil jwtUtil;
    private final JwtFilter jwtFilter;
    private final EmailUtils emailUtils;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ResponseEntity<String> signup(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (this.validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    requestMap.replace("password", bCryptPasswordEncoder.encode(requestMap.get("password")));
                    userDao.save(this.getUserFromMap(requestMap));
                    return FoodUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
                } else {
                    return FoodUtils.getResponseEntity(FoodConstants.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
                }

            } else {
                return FoodUtils.getResponseEntity(FoodConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if (authentication.isAuthenticated()) {
                if (customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
                                    customerUsersDetailsService.getUserDetail().getRole())
                            + "\", \"message\":\"" + "Login Success" + "\"}", HttpStatus.OK);
                }
                return new ResponseEntity<String>("{\"message\":\"" + "Wait for admin approval." + "\"}",
                        HttpStatus.BAD_REQUEST);

            }

        } catch (Exception exception) {
            log.error("{}", exception);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad credentials." + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try {
            if (jwtFilter.isAdmin())
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            else
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> user = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (user.isPresent()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    this.sendMailToAllAdmin(requestMap.get("status"), user.get().getEmail(), userDao.getAllAdmin());
                    return FoodUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
                } else {
                    return FoodUtils.getResponseEntity("User id does not exist", HttpStatus.NO_CONTENT);
                }
            } else
                return FoodUtils.getResponseEntity(FoodConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        System.out.println("checkToken");
        return FoodUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            System.out.println("herrreeee");
            User user = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (user != null) {
                if (user.getPassword().equalsIgnoreCase(requestMap.get("oldPassword"))) {
                    user.setPassword(requestMap.get("newPassword"));
                    userDao.save(user);
                    return FoodUtils.getResponseEntity("Password has been updated successfully", HttpStatus.OK);
                }
                return FoodUtils.getResponseEntity("Old password is not true", HttpStatus.BAD_REQUEST);
            }
            return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
                emailUtils.forgotMail(user.getEmail(), "Credentials by Food Ordering System", user.getPassword());
            return FoodUtils.getResponseEntity("Check your email for credentials", HttpStatus.BAD_GATEWAY);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return FoodUtils.getResponseEntity(FoodConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true"))
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved",
                    "User:- " + user + "\n is approved by \nADMIN:- " + jwtFilter.getCurrentUser(), allAdmin);
        else
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled",
                    "User:- " + user + "\n is disabled by \nADMIN:- " + jwtFilter.getCurrentUser(), allAdmin);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name")
                && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email")
                && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
}