package zip.ootd.ootdzip.security;

import static org.assertj.core.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.oauth.domain.UserAuthenticationToken;

public class JwtUtilsTest {

    private final JwtUtils jwtUtils;

    public JwtUtilsTest() {
        this.jwtUtils = new JwtUtils("ABCD1234567890123456789012345678901234567890", "HS256", 3600000L, 86400000L);
    }

    @Test
    @DisplayName("JwtUtils 초기화 테스트")
    void jwtUtilsInits() {
        assertThat(jwtUtils).isNotNull();
        assertThat(jwtUtils).isInstanceOf(JwtUtils.class);
    }

    @Test
    @DisplayName("JWT TokenInfo build 성공")
    void buildTokenInfo() {
        UserAuthenticationToken.UserDetails userDetails = new UserAuthenticationToken.UserDetails(1580L);
        Authentication authentication = new UserAuthenticationToken(userDetails);
        TokenInfo token = jwtUtils.buildTokenInfo(authentication);

        assertThat(token.getTokenType()).isEqualToIgnoringCase("Bearer");
        assertThat(token.getExpiresIn()).isEqualTo(3600L);
        assertThat(token.getRefreshTokenExpiresIn()).isEqualTo(86400L);
    }

    @Test
    @DisplayName("JWT encode 성공")
    void encodeJwt() {
        UserAuthenticationToken.UserDetails userDetails = new UserAuthenticationToken.UserDetails(1580L);
        Authentication authentication = new UserAuthenticationToken(userDetails);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2020, Calendar.NOVEMBER, 15, 10, 50, 17);
        calendar.set(Calendar.MILLISECOND, 0);
        Date time = calendar.getTime();
        String encoded = jwtUtils.encode(authentication, time, 86400000L);

        @SuppressWarnings("checkstyle:LineLength")
        String expected = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJvb3RkLnppcCIsInN1YiI6IjE1ODAiLCJpYXQiOjE2MDU0MDUwMTcsImV4cCI6MTYwNTQ5MTQxN30.mXWRXo68XS94jxrNkmc9C0qicfBq5Db8PqK19W2yBQA";
        assertThat(encoded).isEqualTo(expected);
    }

    @Test
    @DisplayName("JWT decode 성공")
    void decodeJwt() {
        UserAuthenticationToken.UserDetails userDetails = new UserAuthenticationToken.UserDetails(1580L);
        Authentication authentication = new UserAuthenticationToken(userDetails);
        // Cannot use fixed jwt value since it expires after some amount of time.
        Authentication decoded = jwtUtils.decode(jwtUtils.encode(authentication, new Date(), 60000L));
        assertThat(decoded.getName()).isEqualTo("1580");
    }

    @Test
    @DisplayName("JWT decode 실패: verify signature 불일치")
    void decodeWrongVeritySignatureJwt() {
        @SuppressWarnings("checkstyle:LineLength")
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJvb3RkLnppcCIsInN1YiI6IjE1ODAiLCJpYXQiOjE2ODg0NjAzOTEsImV4cCI6MTY4ODQ2Mzk5MX0.pIse_4MuOMZIIQgywdHg18wVF6ynQCL3s5cgYXn9wDY";
        Authentication authentication = jwtUtils.decode(token);
        assertThat(authentication).isNull();
    }

    @Test
    @DisplayName("JWT decode 실패: 잘못된 문자열 형식")
    void decodeEmptyJwt() {
        @SuppressWarnings("checkstyle:LineLength")
        String token = "eyJhbGciOiJIUzI1NiJ9.eyAiaGVsbG8iOiAxMTEsICJieWUiOiAid29ybGQiOiB7IiJ9IHs=.pIse_4MuOMZIIQgywdHg18wVF6ynQCL3s5cgYXn9wDY";
        Authentication authentication = jwtUtils.decode(token);
        assertThat(authentication).isNull();
    }

}
