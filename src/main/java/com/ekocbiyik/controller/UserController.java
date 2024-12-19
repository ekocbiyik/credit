package com.ekocbiyik.controller;

import com.ekocbiyik.model.dto.UserDTO;
import com.ekocbiyik.repository.IUserRepository;
import com.ekocbiyik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.createUser(userDTO));
    }

    @GetMapping("/list")
    public ResponseEntity list() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

}
