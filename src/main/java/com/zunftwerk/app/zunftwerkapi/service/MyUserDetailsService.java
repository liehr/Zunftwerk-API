package com.zunftwerk.app.zunftwerkapi.service;

import com.zunftwerk.app.zunftwerkapi.model.User;
import com.zunftwerk.app.zunftwerkapi.model.UserPrincipal;
import com.zunftwerk.app.zunftwerkapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public MyUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOptional = repository.findUserByEmail(username);

        if (userOptional.isEmpty())
        {
            log.error("User with username {} not found!", username);
            throw new UsernameNotFoundException(username);
        }



        return new UserPrincipal(userOptional.get());
    }
}
