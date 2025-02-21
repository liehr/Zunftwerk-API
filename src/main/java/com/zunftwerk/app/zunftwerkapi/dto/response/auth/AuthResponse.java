package com.zunftwerk.app.zunftwerkapi.dto.response.auth;

public record AuthResponse(String jwtToken, String refreshToken) {
}
