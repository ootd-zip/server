package zip.ootd.ootdzip.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import zip.ootd.ootdzip.oauth.data.KakaoAccessTokenInfoRes;
import zip.ootd.ootdzip.oauth.data.KakaoOauthTokenRes;

@Component
public class KakaoOAuthUtils {

    private final String kakaoAppRestApiKey;

    public KakaoOAuthUtils(@Value("${security.oauth.kakao.app-rest-api-key}") String kakaoAppRestApiKey) {
        this.kakaoAppRestApiKey = kakaoAppRestApiKey;
    }

    public KakaoOauthTokenRes requestTokenByAuthorizationCode(String authorizationCode, String redirectUri) {
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
            ResponseEntity<KakaoOauthTokenRes> response = restTemplate.postForEntity(url, kakaoRequest, KakaoOauthTokenRes.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else { // TODO: 자주 발생하는 오류 코드 KOE320 (잘못된 auth code)
                throw new IllegalStateException("카카오 토큰 발급 중 오류 발생: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            throw new IllegalStateException("카카오 토큰 발급 중 오류 발생: " + exception.getMessage());
        }
    }

    public Long requestAccessTokenMemberId(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/access_token_info";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> kakaoRequest = new HttpEntity<>(headers);

        ResponseEntity<KakaoAccessTokenInfoRes> response = restTemplate.exchange(url, HttpMethod.GET, kakaoRequest, KakaoAccessTokenInfoRes.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getId();
        } else {
            throw new IllegalStateException("카카오 토큰 조회 중 오류 발생: " + response.getStatusCode());
        }
    }

}
