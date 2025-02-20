package com.zunftwerk.app.zunftwerkapi.dto.response.organization;

public record OrganizationRegistrationResponse(
        Long organizationId,
        String organizationName,
        String subscriptionPlanName,
        String token,
        String refreshToken,
        String adminEmail
) {}

