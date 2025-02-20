package com.zunftwerk.app.zunftwerkapi.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private Long organizationId;
    private List<String> roles;
    private List<String> modules;

    public CustomAuthenticationToken(Object principal, Object credentials,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

}

