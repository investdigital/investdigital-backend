package com.oxchains.message.auth;

import com.oxchains.message.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

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
        return emptyList();
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


    @Override
    public String toString() {
        return token;
    }

}
