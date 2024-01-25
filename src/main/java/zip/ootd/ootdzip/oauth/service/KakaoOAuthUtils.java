package zip.ootd.ootdzip.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.KakaoAccessTokenInfoRes;
import zip.ootd.ootdzip.oauth.data.KakaoOauthTokenRes;

@Component
@Slf4j
public class KakaoOAuthUtils implements SocialOAuth {

    private final String kakaoAppRestApiKey;

    public KakaoOAuthUtils(
            @Value("${spring.security.oauth2.client.registration.kakao.app-rest-api-key}") String kakaoAppRestApiKey) {
        this.kakaoAppRestApiKey = kakaoAppRestApiKey;
    }

    private KakaoOauthTokenRes requestTokenByAuthorizationCode(String authorizationCode, String redirectUri) {
        String url = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("grant_type", "authorization_code");
        requestData.add("client_id", kakaoAppRestApiKey);
        requestData.add("redirect_uri", redirectUri);
        requestData.add("code", authorizationCode);
        HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(requestData, headers);

        try {
            ResponseEntity<KakaoOauthTokenRes> response = restTemplate.postForEntity(url, kakaoRequest,
                    KakaoOauthTokenRes.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new CustomException(ErrorCode.KAKAO_LOGIN_ERROR);
            }
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            log.error("kakao error message: " + exception);
            throw new CustomException(ErrorCode.KAKAO_LOGIN_ERROR);
        }
    }

    private String getAccessToken(String authorizationCode, String redirectUri) {
        return requestTokenByAuthorizationCode(authorizationCode, redirectUri).getAccess_token();
    }

    private Long requestAccessTokenMemberId(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/access_token_info";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> kakaoRequest = new HttpEntity<>(headers);

        ResponseEntity<KakaoAccessTokenInfoRes> response = restTemplate.exchange(url, HttpMethod.GET, kakaoRequest,
                KakaoAccessTokenInfoRes.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getId();
        } else {
            throw new IllegalStateException("카카오 토큰 조회 중 오류 발생: " + response.getStatusCode());
        }
    }

    @Override
    public String getSocialIdBy(String... args) {
        String accessToken = getAccessToken(args[0], args[1]);
        return String.valueOf(requestAccessTokenMemberId(accessToken));
    }
}
