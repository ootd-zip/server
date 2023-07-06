package zip.ootd.ootdzip.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("findAll이 작동해야 함")
    void findAllUser() {
        List<User> result = userRepository.findAll();
        assertThat(result).isInstanceOf(List.class);
    }

    @Test
    @DisplayName("user 정보 저장 및 불러오기 테스트")
    void findUserById() {
        String userName = "test user name";
        UserGender gender = UserGender.MALE;
        LocalDate birthdate =LocalDate.of(2000, 1, 1);
        Integer userHeight = 180;
        User saved = userRepository.save(new User(userName,
                gender,
                birthdate,
                userHeight,
                true,
                80,
                true,
                null,
                false));

        Optional<User> result = userRepository.findById(saved.getId());
        assertThat(result).hasValueSatisfying((value) -> assertThat(value.getName()).isEqualTo(userName));
        assertThat(result).hasValueSatisfying((value) -> assertThat(value.getGender()).isEqualTo(gender));
        assertThat(result).hasValueSatisfying((value) -> assertThat(value.getBirthdate()).isEqualTo(birthdate));
        assertThat(result).hasValueSatisfying((value) -> assertThat(value.getHeight()).isEqualTo(userHeight));
    }
}
