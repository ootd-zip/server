package zip.ootd.ootdzip.oauth.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.data.AuthorizedUser;

@Service
@RequiredArgsConstructor
public class AuthorizedOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final AuthorizedUserService authorizedUserService;

    private final OidcUserService delegate = new OidcUserService();

    @Override
    public AuthorizedUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(userRequest);

        return authorizedUserService.getAuthorizedUser(userRequest, oidcUser);
    }
}
