package com.zunftwerk.app.zunftwerkapi.provider;

import com.zunftwerk.app.zunftwerkapi.model.User;
import com.zunftwerk.app.zunftwerkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserProvider {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found.");
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userRepository.findUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User with email " + userDetails.getUsername() + " not found."));
        }

        throw new IllegalStateException("User details not found in authentication.");
    }
}
