package com.zunftwerk.app.zunftwerkapi.controller;

import com.zunftwerk.app.zunftwerkapi.dto.request.auth.LoginRequest;
import com.zunftwerk.app.zunftwerkapi.dto.request.auth.RefreshRequest;
import com.zunftwerk.app.zunftwerkapi.dto.request.organization.CreateOrganizationRequest;
import com.zunftwerk.app.zunftwerkapi.dto.response.auth.AuthResponse;
import com.zunftwerk.app.zunftwerkapi.dto.response.organization.OrganizationRegistrationResponse;
import com.zunftwerk.app.zunftwerkapi.service.AuthService;
import com.zunftwerk.app.zunftwerkapi.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final OrganizationService organizationService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<OrganizationRegistrationResponse> signup(@RequestBody CreateOrganizationRequest request) {
        return new ResponseEntity<>(organizationService.createOrganizationAndAdminAccount(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return new ResponseEntity<>(authService.verify(request), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        AuthResponse response = authService.refreshToken(request.refreshToken());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
