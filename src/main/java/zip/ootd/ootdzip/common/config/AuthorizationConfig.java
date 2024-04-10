package zip.ootd.ootdzip.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.CustomAuthorizationCodeGrantRequestEntityConverter;
import zip.ootd.ootdzip.oauth.OAuth2AuthenticationSuccessHandler;
import zip.ootd.ootdzip.oauth.repository.InMemoryOAuth2AuthorizationRequestRepository;
import zip.ootd.ootdzip.oauth.service.TokenService;

@Configuration
@RequiredArgsConstructor
public class AuthorizationConfig {

    private static final String authorizationUri = "/api/v1/login/authorization";

    private static final String redirectionUri = "/api/v1/login/oauth/code/*";

    private final CorsConfigurationSource corsConfigurationSource;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(authorizationUri + "/*", redirectionUri)
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri(authorizationUri)
                                .authorizationRequestRepository(authorizationRequestRepository()))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri(redirectionUri))
                        .tokenEndpoint(token -> token
                                .accessTokenResponseClient(accessTokenResponseClient()))
                        .successHandler(successHandler(tokenService, objectMapper)))
                .cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler successHandler(TokenService tokenService, ObjectMapper objectMapper) {
        return new OAuth2AuthenticationSuccessHandler(tokenService, objectMapper);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new InMemoryOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> requestEntityConverter() {
        return new CustomAuthorizationCodeGrantRequestEntityConverter();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        client.setRequestEntityConverter(requestEntityConverter());

        return client;
    }
}
