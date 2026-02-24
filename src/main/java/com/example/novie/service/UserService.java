package com.example.novie.service;

import com.example.novie.exception.InformationExistException;
import com.example.novie.exception.InformationNotFoundException;

import com.example.novie.model.User;
import com.example.novie.model.request.LoginRequest;
import com.example.novie.model.response.LoginResponse;
import com.example.novie.repository.UserRepository;
import com.example.novie.security.JWTUtils;
import com.example.novie.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private MyUserDetails myUserDetails;



    @Autowired
    public UserService(UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetails myUserDetails) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.myUserDetails = myUserDetails;
    }

    // ================= CREATE USER =================
    public User createUser(User userObject) {
        if (!userRepository.existsByEmailAddress(userObject.getEmailAddress())) {

            userObject.setPassword(passwordEncoder.encode(userObject.getPassword()));
            userObject.setActive(true);
            userObject.setAccountVerified(true);

            User result = userRepository.save(userObject);
            return result;

        } else {
            throw new InformationExistException("User already exists");
        }
    }



    // ================= LOGIN =================
    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            myUserDetails = (MyUserDetails) authentication.getPrincipal();

            if (!myUserDetails.isActive()) {
                return ResponseEntity.badRequest()
                        .body(new LoginResponse("Account is deactivated"));
            }


            final String JWT = jwtUtils.generateJwtToken(myUserDetails);
            return ResponseEntity.ok(new LoginResponse(JWT));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Error: Username or password is incorrect"));
        }
    }

    // ================= CHANGE PASSWORD =================
    public void changePassword(String oldPassword, String newPassword) {

        User user = myUserDetails.getUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(String emailAddress, String newPassword) {

        User user = userRepository.findUserByEmailAddress(emailAddress);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ================= SOFT DELETE =================
    public void softDeleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("User not found"));

        user.setActive(false);
        userRepository.save(user);
    }

    // ================= FIND USER =================
    public User findUserByEmailAddress(String email) {
        return userRepository.findUserByEmailAddress(email);
    }
}