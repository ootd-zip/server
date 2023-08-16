package zip.ootd.ootdzip.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.GoogleAccessTokenInfoRes;
import zip.ootd.ootdzip.oauth.data.GoogleOauthToken;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthUtils implements SocialOAuth {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.redirect_uri}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    private final ObjectMapper objectMapper;

    private ResponseEntity<String> requestTokenByAuthorizationCode(String authorizationCode) {
        String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", authorizationCode);
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.put("grant_type", "authorization_code");

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, params, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response;
            } else {
                throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
            }
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
        }
    }

    private String getAccessToken(ResponseEntity<String> response) {
        try {
            return objectMapper.readValue(response.getBody(), GoogleOauthToken.class).getAccess_token();
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
        }
    }

    private GoogleAccessTokenInfoRes requestMemberInfo(String accessToken) {
        String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
        try {
            if (response.getStatusCode().is2xxSuccessful()) {
                return objectMapper.readValue(response.getBody(), GoogleAccessTokenInfoRes.class);
            } else {
                throw new CustomException(ErrorCode.GOOGLE_LOGIN_ERROR);
            }
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