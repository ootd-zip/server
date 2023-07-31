package com.github.prgrms.configures;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtTokenConfigure {

    private String header;

    private String issuer;

    private String clientSecret;

    private int expirySeconds;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public int getExpirySeconds() {
        return expirySeconds;
    }

    public void setExpirySeconds(int expirySeconds) {
        this.expirySeconds = expirySeconds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("header", header)
                .append("issuer", issuer)
                .append("clientSecret", clientSecret)
                .append("expirySeconds", expirySeconds)
                .toString();
    }

}

/*
 * spring:
 *   application:
 *     name: programmers spring assignments
 *   messages:
 *     basename: i18n/messages
 *     encoding: UTF-8
 *     cache-duration: PT1H
 *   h2:
 *     console:
 *       enabled: true
 *       path: /h2-console
 *   datasource:
 *     platform: h2
 *     driver-class-name: org.h2.Driver
 *     url: "jdbc:h2:mem:spring_assignments;MODE=MYSQL;DB_CLOSE_DELAY=-1"
 *     username: sa
 *     password:
 *     hikari:
 *       minimum-idle: 1
 *       maximum-pool-size: 5
 *       pool-name: H2_DB
 * server:
 *   port: 5000
 * jwt:
 *   token:
 *     header: X-PRGRMS-AUTH
 *     issuer: programmers
 *     client-secret: Rel3Bjce2MajBo09qgkNgYaTuzvJe8iwnBFhsDS5
 *     expiry-seconds: 0
 */