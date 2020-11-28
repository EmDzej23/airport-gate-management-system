package com.mj.airport.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mj.airport.model.User;

import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import lombok.*;

@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "username")
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private User user;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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

    public static UserPrincipal build(User user) {
        return new UserPrincipal(
                user,
                Arrays.asList(new SimpleGrantedAuthority(Constants.ADMIN)));

    }
}
