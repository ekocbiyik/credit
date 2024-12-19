package com.ekocbiyik.security;

import com.ekocbiyik.exception.BadCredentialsException;
import com.ekocbiyik.model.entities.User;
import com.ekocbiyik.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("invalidUsernameOrPassword");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getName())));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,       // accountNonExpired
                true,     // credentialsNonExpired
                true,       // accountNonLocked
                authorities
        );
    }
}
