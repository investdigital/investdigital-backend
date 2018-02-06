package com.oxchains.rmsuser.auth;

import com.oxchains.rmsuser.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * @author aiet
 */
public class JwtAuthentication implements Authentication {

    private String token;
    private User user;
    private Map<String, Object> details;

    JwtAuthentication(User user, String token, Map<String, Object> details) {
        this.user = user;
        this.token = token;
        this.details = details;
    }

    public Optional<User> user() {
        return Optional.ofNullable(user);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO
        return user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        //return emptyList();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return details;
    }


    @Override
    public Object getPrincipal() {
        return user;
        //return null;
    }

    @Override
    public boolean isAuthenticated() {
        return user != null && (user.getLoginname() != null || user.getEmail() != null || user.getMobilephone() != null);
        // return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (!isAuthenticated) {
            user = null;
        }
    }

    @Override
    public String getName() {
        if (user != null) {
            return user.getUsername();
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return token;
    }

}
