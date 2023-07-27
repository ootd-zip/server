package zip.ootd.ootdzip.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import zip.ootd.ootdzip.oauth.data.KakaoOAuthTokenReq;
import zip.ootd.ootdzip.oauth.data.KakaoOAuthTokenRes;

@Component
public class KakaoOAuthUtils {

    private final String kakaoAppRestApiKey;

    public KakaoOAuthUtils(@Value("${oauth.provider.kakao.app-rest-api-key}") String kakaoAppRestApiKey) {
        this.kakaoAppRestApiKey = kakaoAppRestApiKey;
    }

    public KakaoOAuthTokenRes requestTokenByAuthorizationCode(String authorizationCode, String redirectUri) {
        String url = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        KakaoOAuthTokenReq requestBody = KakaoOAuthTokenReq.builder()
                .grantType("authorization_code")
                .clientId(kakaoAppRestApiKey)
                .redirectUri(redirectUri)
                .code(authorizationCode)
                .build();
        HttpEntity<KakaoOAuthTokenReq> kakaoRequest = new HttpEntity<>(requestBody, headers);

        ResponseEntity<KakaoOAuthTokenRes> response = restTemplate.postForEntity(url, kakaoRequest, KakaoOAuthTokenRes.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new IllegalStateException("카카오 토큰 발급 중 오류 발생: " + response.getStatusCode());
        }
    }

}
