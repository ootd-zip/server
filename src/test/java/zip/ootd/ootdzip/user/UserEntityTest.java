package zip.ootd.ootdzip.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserEntityTest {

    @Test
    @DisplayName("User 기본 성별이 UNKNOWN")
    void userDefaultGenderTest() {
        User user = new User();

        Assertions.assertThat(user.getGender()).isEqualTo(UserGender.UNKNOWN);
    }

    @Test
    @DisplayName("User 기본 삭제 상태가 false")
    void userDefaultIsDeletedTest() {
        User user = new User();

        Assertions.assertThat(user.getIsDeleted()).isEqualTo(false);
    }
}
