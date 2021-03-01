package webServer.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;

import java.util.Collection;

/**
 * The purpose of this is to make the user connected to the call when using api keys.
 *
 * Avoiding exposing anything other than userId
 * */
@Transient
public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    long userId;
    public ApiKeyAuthentication(long userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
