package com.zunftwerk.app.zunftwerkapi.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final transient User user;
    @Getter
    private final Long organizationId;
    @Getter
    private final List<String> roles;
    @Getter
    private final List<String> modules;

    public UserPrincipal(User user, Long organizationId, List<String> roles, List<String> modules) {
        this.user = user;
        this.organizationId = organizationId;
        this.roles = roles;
        this.modules = modules;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mappen der Rollen in GrantedAuthority-Objekte
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
