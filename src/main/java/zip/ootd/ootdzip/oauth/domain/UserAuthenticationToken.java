package zip.ootd.ootdzip.oauth.domain;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails principal;

    public UserAuthenticationToken(UserDetails principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserDetails getPrincipal() {
        return this.principal;
    }

    @Override
    public String getName() {
        return String.valueOf(this.principal.getId());
    }

    @Getter
    @AllArgsConstructor
    public static class UserDetails {

        public Long id;
    }
}
