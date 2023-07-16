package zip.ootd.ootdzip.oauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticationTokenTest {

    @Test
    @DisplayName("getName이 String 형식의 id를 반환해야 함")
    void userAuthGetNameTest() {
        UserAuthenticationToken.UserDetails userDetails = new UserAuthenticationToken.UserDetails(12345L);
        Authentication authentication = new UserAuthenticationToken(userDetails);
        assertThat(authentication.getName()).isEqualTo("12345");
    }
}