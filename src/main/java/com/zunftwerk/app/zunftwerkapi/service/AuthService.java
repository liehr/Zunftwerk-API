package com.zunftwerk.app.zunftwerkapi.service;

import com.zunftwerk.app.zunftwerkapi.dto.request.auth.LoginRequest;
import com.zunftwerk.app.zunftwerkapi.dto.response.auth.AuthResponse;
import com.zunftwerk.app.zunftwerkapi.model.Module;
import com.zunftwerk.app.zunftwerkapi.model.User;
import com.zunftwerk.app.zunftwerkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApplicationContext applicationContext;

    public AuthResponse verify(LoginRequest request) {
        return userRepository.findUserByEmail(request.email())
                .map(user -> {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));
                    if (authentication.isAuthenticated()) {
                        String jwtToken = jwtService.generateToken(
                                user.getEmail(),
                                user.getOrganization().getId(),
                                List.of(user.getRole()),
                                user.getOrganization()
                                        .getEffectiveModules()
                                        .stream()
                                        .map(Module::getModuleName)
                                        .toList());
                        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
                        return new AuthResponse(jwtToken, refreshToken);
                    }
                    log.error("Authentication failed for user: {}", user.getEmail());
                    throw new IllegalStateException("Authentication failed for user: " + user.getEmail());
                })
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + request.email() + " not found"));
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUserName(refreshToken);
        return userRepository.findUserByEmail(username)
                .map(user -> {
                    UserDetails userDetails = applicationContext.getBean(MyUserDetailsService.class)
                            .loadUserByUsername(username);
                    if (!jwtService.validateToken(refreshToken, userDetails)) {
                        throw new IllegalStateException("Invalid refresh token");
                    }
                    String newAccessToken = jwtService.generateToken(
                            user.getEmail(),
                            user.getOrganization().getId(),
                            List.of(user.getRole()),
                            user.getOrganization().getEffectiveModules()
                                    .stream()
                                    .map(Module::getModuleName)
                                    .toList());

                    long remainingTimeMillis = jwtService.extractExpiration(refreshToken).getTime() - System.currentTimeMillis();
                    long oneHourMillis = TimeUnit.HOURS.toMillis(1);
                    String finalRefreshToken = remainingTimeMillis <= oneHourMillis
                            ? jwtService.generateRefreshToken(user.getEmail())
                            : refreshToken;

                    return new AuthResponse(newAccessToken, finalRefreshToken);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " not found"));
    }
}
