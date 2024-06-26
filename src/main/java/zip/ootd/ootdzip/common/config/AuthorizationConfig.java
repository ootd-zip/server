package zip.ootd.ootdzip.common.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.OAuth2AuthenticationFailureHandler;
import zip.ootd.ootdzip.oauth.OAuth2AuthenticationSuccessHandler;
import zip.ootd.ootdzip.oauth.RedirectUriParameterOAuth2AuthorizationRequestResolver;
import zip.ootd.ootdzip.oauth.provider.ClientSecretGenerator;
import zip.ootd.ootdzip.oauth.repository.InMemoryDynamicClientRegistrationRepository;
import zip.ootd.ootdzip.oauth.repository.InMemoryOAuth2AuthorizationRequestRepository;
import zip.ootd.ootdzip.oauth.service.TokenService;

@Configuration
@RequiredArgsConstructor
public class AuthorizationConfig {

    private static final String authorizationUri = "/api/v1/login/authorization";

    private static final String redirectionUri = "/api/v1/login/oauth/code/*";

    private static final List<String> redirectUriParameterAllowedRegistrationIds = List.of("dev");

    private final OAuth2ClientProperties clientProperties;
    private final CorsConfigurationSource corsConfigurationSource;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final List<ClientSecretGenerator> secretGenerators;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(authorizationUri + "/*", redirectionUri)
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri(authorizationUri)
                                .authorizationRequestResolver(oauth2AuthorizationRequestResolver(
                                        clientRegistrationRepository(clientProperties)))
                                .authorizationRequestRepository(authorizationRequestRepository()))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri(redirectionUri))
                        .successHandler(successHandler(tokenService, objectMapper))
                        .failureHandler(failureHandler(objectMapper)))
                .cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler successHandler(TokenService tokenService, ObjectMapper objectMapper) {
        return new OAuth2AuthenticationSuccessHandler(tokenService, objectMapper);
    }

    @Bean
    public OAuth2AuthenticationFailureHandler failureHandler(ObjectMapper objectMapper) {
        return new OAuth2AuthenticationFailureHandler(objectMapper);
    }

    @Bean
    public OAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new RedirectUriParameterOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                authorizationUri, redirectUriParameterAllowedRegistrationIds);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new InMemoryOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        Map<String, ClientRegistration> registrations = new OAuth2ClientPropertiesMapper(
                properties).asClientRegistrations();
        return new InMemoryDynamicClientRegistrationRepository(registrations, secretGenerators);
    }
}
