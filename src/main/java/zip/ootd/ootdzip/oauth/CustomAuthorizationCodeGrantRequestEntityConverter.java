package zip.ootd.ootdzip.oauth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;

import zip.ootd.ootdzip.oauth.provider.AppleClientRegistration;

public class CustomAuthorizationCodeGrantRequestEntityConverter
        implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private final OAuth2AuthorizationCodeGrantRequestEntityConverter delegate = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    @Override
    public RequestEntity<?> convert(@NonNull OAuth2AuthorizationCodeGrantRequest source) {
        RequestEntity<?> request = delegate.convert(source);

        String registrationId = source.getClientRegistration().getRegistrationId();
        // Apple ID일 경우 직접 생성한 client_secret 설정
        if (AppleClientRegistration.getRegistrationId().equals(registrationId)) {
            MultiValueMap<String, String> body = (MultiValueMap<String, String>)request.getBody();
            body.set(OAuth2ParameterNames.CLIENT_SECRET, AppleClientRegistration.getClientSecret());
            return new RequestEntity<>(body, request.getHeaders(), request.getMethod(), request.getUrl());
        }

        return request;
    }
}
