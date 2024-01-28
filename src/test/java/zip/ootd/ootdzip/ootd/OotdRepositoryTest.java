package zip.ootd.ootdzip.ootd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.user.repository.UserRepository;

@DataJpaTest
public class OotdRepositoryTest {
    @Autowired
    private OotdRepository ootdRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("게시글 작성 테스트")
    void writeBoardTest() {
        //given
        // String userName = "test user name";
        // UserGender gender = UserGender.MALE;
        // LocalDate birthdate = LocalDate.of(2000, 1, 1);
        // Integer userHeight = 180;
        // User writer = userRepository.save(new User(userName,
        //         gender,
        //         birthdate,
        //         userHeight,
        //         true,
        //         80,
        //         true,
        //         null,
        //         false,
        //         false));
        //
        // String contents = "test contents";
        // List<OotdImage> ootdImages = new ArrayList<>();
        // List<OotdImageClothes> ootdImageClothes = new ArrayList<>();
        // List<OotdStyle> ootdStyles = new ArrayList<>();
        //
        // //when
        // Ootd ootd = Ootd.createOotd(writer, contents, UserGender.MALE, true, ootdImages, ootdImageClothes,
        //         ootdStyles);
        // Ootd savedOotd = ootdRepository.save(ootd);
        // //then
        // assertThat(savedOotd).isEqualTo(ootd);

    }
}
