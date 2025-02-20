package com.zunftwerk.app.zunftwerkapi.controller;

import com.zunftwerk.app.zunftwerkapi.dto.request.organization.CreateOrganizationRequest;
import com.zunftwerk.app.zunftwerkapi.dto.response.organization.OrganizationRegistrationResponse;
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

    @PostMapping("/signup")
    public ResponseEntity<OrganizationRegistrationResponse> singup(@RequestBody CreateOrganizationRequest request) {
        return new ResponseEntity<>(organizationService.createOrganizationAndAdminAccount(request), HttpStatus.CREATED);
    }
}
