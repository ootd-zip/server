package zip.ootd.ootdzip.security;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

class JwtAuthenticationFilterTest {

    private final JwtUtils jwtUtils;
    private final JwtAuthenticationFilter filter;

    public JwtAuthenticationFilterTest() {
        this.jwtUtils = new JwtUtils(
                "ABCD1234567890123456789012345678901234567890",
                "HS256",
                3600000L,
                86400000L);
        this.filter = new JwtAuthenticationFilter(jwtUtils);
    }

    @Test
    @DisplayName("getAccessToken 리턴값이 Access Token 이어야 함")
    void getAccessTokenTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer TEST_ACCESS_TOKEN_VALUE");

        Method getAccessToken = filter.getClass().getDeclaredMethod("getAccessToken", HttpServletRequest.class);
        getAccessToken.setAccessible(true);
        Object result = getAccessToken.invoke(filter, request);

        assertThat(result).isEqualTo("TEST_ACCESS_TOKEN_VALUE");
    }
}
