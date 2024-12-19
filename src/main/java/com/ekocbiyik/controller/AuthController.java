package com.ekocbiyik.controller;

import com.ekocbiyik.model.dto.UserDTO;
import com.ekocbiyik.model.entities.Role;
import com.ekocbiyik.model.entities.User;
import com.ekocbiyik.repository.IUserRepository;
import com.ekocbiyik.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> credentials) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            throw new BadCredentialsException("invalidUsernameOrPassword");
        }

        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userRepository.findByUsername(username);
        user.setUpdatedDate(OffsetDateTime.now());
        userRepository.save(user); // just for last login

        UserDTO userDto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())) // olması gereken roleId bilgisinin iletilmesi.
                .build();

        Map<String, Object> creds = new HashMap<>();
        creds.put("token", jwtUtil.generateToken(user));
        creds.put("user", userDto);
        return ResponseEntity.status(HttpStatus.OK).body(creds);
    }

}
