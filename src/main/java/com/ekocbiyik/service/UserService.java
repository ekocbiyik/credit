package com.ekocbiyik.service;

import com.ekocbiyik.exception.BadRequestException;
import com.ekocbiyik.model.dto.UserDTO;
import com.ekocbiyik.model.entities.Role;
import com.ekocbiyik.model.entities.User;
import com.ekocbiyik.repository.IRoleRepository;
import com.ekocbiyik.repository.IUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
@Service
public class UserService {

    private IUserRepository userRepository;
    private IRoleRepository roleRepository;

    public UserService(IUserRepository userRepository,
                       IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDTO createUser(UserDTO userDTO) {

        if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            throw new BadRequestException("Username or Password cannot be empty!");
        }

        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            throw new BadRequestException("Roles cannot be empty!");
        }

        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user != null) {
            throw new BadRequestException("User already exists!");
        }

        List<Role> roleList = roleRepository.findAllByName(userDTO.getRoles().stream().toList());
        if (roleList.size() != userDTO.getRoles().size()) {
            throw new BadRequestException("Roles does not match!");
        }

        user = User.builder()
                .username(userDTO.getUsername())
                .password(new BCryptPasswordEncoder().encode(userDTO.getPassword()))
                .enabled(true)
                .roles(new HashSet<>(roleList))
                .build();

        userRepository.save(user);
        userDTO.setId(user.getId());
        return userDTO;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .enabled(user.isEnabled())
                        .roles(user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

}
