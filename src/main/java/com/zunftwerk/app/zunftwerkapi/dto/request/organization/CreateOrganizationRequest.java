package com.zunftwerk.app.zunftwerkapi.dto.request.organization;

public record CreateOrganizationRequest(String organizationName, String adminEmail, String adminPassword) {
}
