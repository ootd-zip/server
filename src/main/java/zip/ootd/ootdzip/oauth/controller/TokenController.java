package zip.ootd.ootdzip.oauth.controller;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.oauth.data.TokenResponse;
import zip.ootd.ootdzip.oauth.service.LoginService;
import zip.ootd.ootdzip.oauth.service.TokenService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Token 컨트롤러", description = "로그인 및 토큰 관련 API입니다.")
public class TokenController {

    private final TokenService tokenService;
    private final LoginService loginService;

    // Swagger 문서용 더미 메소드, 실제로는 AuthoriationConfig에서 처리.
    @Operation(summary = "소셜 로그인",
            description = "해당 경로에 접속하여 로그인 과정 시작, provider=dev일 경우 redirect_uri 전달 가능",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "302")
            })
    @GetMapping("/api/v1/login/authorization/{provider}")
    public void login(@PathVariable("provider") String provider, @RequestParam("redirect_uri") String redirectUri) {
        throw new NotImplementedException();
    }

    // Swagger 문서용 더미 메소드, 실제로는 AuthoriationConfig에서 처리.
    @Operation(summary = "코드로 토큰 변환", description = "로그인 요청에서 callback 경로로 전달된 query params를 전달하여 토큰 발급")
    @GetMapping("/api/v1/login/oauth/code/{provider}")
    public TokenResponse code(@PathVariable("provider") String provider, @RequestParam("code") String code,
            @RequestParam("state") String state) {
        throw new NotImplementedException();
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 액세스 및 리프레시 토큰을 재발급하고 기존 리프레시 토큰은 무효화")
    @PostMapping(value = "/api/v1/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TokenResponse refresh(
            @RequestParam("grantType") String grantType,
            @RequestParam("refreshToken") String refreshToken) {
        if (!OAuth2ParameterNames.REFRESH_TOKEN.equals(grantType)) {
            throw new CustomException(ErrorCode.INVALID_GRANT_TYPE_ERROR);
        }

        return tokenService.refreshAccessToken(refreshToken);
    }

    @Operation(summary = "리프레시 토큰 무효화", description = "리프레시 토큰을 무효화합니다.")
    @PostMapping(value = "/api/v1/oauth/token/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ApiResponse<Void> revoke(@RequestParam("refreshToken") String refreshToken) {
        tokenService.revokeRefreshToken(refreshToken);
        return new ApiResponse<>();
    }

    @Operation(summary = "카카오 코드로 토큰 발급", description = "state 없이 코드를 토큰으로 변환할 때 사용하는 API")
    @PostMapping(value = "/api/v1/oauth/token/code/kakao", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TokenResponse token(@RequestParam("code") String code, @RequestParam("redirect_uri") String redirectUri) {
        return loginService.token("kakao", code, redirectUri);
    }
}
