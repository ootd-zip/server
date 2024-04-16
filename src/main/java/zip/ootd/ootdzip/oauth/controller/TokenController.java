package zip.ootd.ootdzip.oauth.controller;

import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.TokenResponse;
import zip.ootd.ootdzip.oauth.service.TokenService;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping(value = "/api/v1/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TokenResponse token(
            @RequestParam("grantType") String grantType,
            @RequestParam("refreshToken") String refreshToken) {
        if (!OAuth2ParameterNames.REFRESH_TOKEN.equals(grantType)) {
            throw new CustomException(ErrorCode.INVALID_GRANT_TYPE_ERROR);
        }

        return tokenService.refreshAccessToken(refreshToken);
    }
}
