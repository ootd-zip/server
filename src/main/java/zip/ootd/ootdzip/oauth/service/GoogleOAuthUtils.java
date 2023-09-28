package zip.ootd.ootdzip.oauth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.GoogleAccessTokenInfoRes;
import zip.ootd.ootdzip.oauth.data.GoogleOauthToken;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthUtils implements SocialOAuth {

    private final ObjectMapper objectMapper;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_SNS_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.redirect_uri}")
    private String GOOGLE_SNS_CALLBACK_URL;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    private ResponseEntity<String> requestTokenByAuthorizationCode(String authorizationCode) {
        String url = "https://oauth2.googleapis.com/token"; // Google Token Request URL
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", authorizationCode);
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> response = restTemplate.postForEntity(url, params, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
        }
        return response;
    }

    private String getAccessToken(ResponseEntity<String> response) {
        try {
            return objectMapper.readValue(response.getBody(), GoogleOauthToken.class).getAccessToken();
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
        }
    }

    private GoogleAccessTokenInfoRes requestMemberInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo"; // Google UserInfo Request URL
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        headers.setBearerAuth(accessToken);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        try {
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
            }
            return objectMapper.readValue(response.getBody(), GoogleAccessTokenInfoRes.class);

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
        }
    }

    @Override
    public String getSocialIdBy(String... args) {
        String accessToken = getAccessToken(requestTokenByAuthorizationCode(args[0]));
        return requestMemberInfo(accessToken).getId();
    }
}