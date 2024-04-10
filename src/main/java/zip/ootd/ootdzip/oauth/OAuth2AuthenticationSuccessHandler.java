package zip.ootd.ootdzip.oauth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.data.RegisteredOAuth2User;
import zip.ootd.ootdzip.oauth.data.TokenResponse;
import zip.ootd.ootdzip.oauth.service.TokenService;

@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        RegisteredOAuth2User principal = (RegisteredOAuth2User)authentication.getPrincipal();
        TokenResponse tokenResponse = tokenService.issueNewAccessToken(principal.getUser());

        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}
