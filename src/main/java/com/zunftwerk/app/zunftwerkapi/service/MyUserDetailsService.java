package com.zunftwerk.app.zunftwerkapi.service;

import com.zunftwerk.app.zunftwerkapi.model.Module;
import com.zunftwerk.app.zunftwerkapi.model.User;
import com.zunftwerk.app.zunftwerkapi.model.UserPrincipal;
import com.zunftwerk.app.zunftwerkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findUserByEmail(username);

        if (userOptional.isEmpty()) {
            log.error("User with username {} not found!", username);
            throw new UsernameNotFoundException(username);
        }

        User user = userOptional.get();
        Long organizationId = user.getOrganization().getId();
        List<String> roles = List.of(user.getRole());
        List<String> modules = user.getOrganization().getEffectiveModules().stream()
                .map(Module::getModuleName)
                .toList();

        return new UserPrincipal(user, organizationId, roles, modules);
    }
}
