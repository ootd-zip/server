package zip.ootd.ootdzip.ootd.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class OotdServiceTest {

    @InjectMocks
    private OotdService ootdService;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private ClothesRepository clothesRepository;

    @Mock
    private StyleRepository styleRepository;

    @Mock
    private RedisDao redisDao;

    @Mock
    private UserService userService;

    // @Test
    // @DisplayName("OOTD게시판_정상_등록")
    // public void createOotdSuccess() {
    //     // 가짜 데이터 생성
    //     OotdPostReq request = new OotdPostReq();
    //     request.setOotdImages(new ArrayList<>()); // 필요한 이미지 URL 리스트 추가
    //     request.setClotheIds(new ArrayList<>()); // 필요한 의류 ID 리스트 추가
    //     request.setContent("OOTD Content");
    //     request.setGender(UserGender.MALE); // 적절한 성별 설정
    //     request.setStyles(new ArrayList<>());
    //     request.setIsPrivate(true);
    //
    //     // Mock 객체의 동작 설정
    //     List<Clothes> clothesList = new ArrayList<>();
    //     when(clothesRepository.findAllById(request.getClotheIds())).thenReturn(clothesList);
    //
    //     User user = new User();
    //     when(userService.getAuthenticatiedUser()).thenReturn(user);
    //
    //     // Board 객체 생성
    //     Ootd ootd = ootdService.postOotd(request);
    //
    //     // Board 객체가 잘 생성되었는지 검증
    //     assertNotNull(ootd); // Board 객체가 null이 아닌지 확인
    //     assertEquals(user, ootd.getWriter()); // 작성자가 올바른지 확인
    //     assertEquals(request.getContent(), ootd.getContents()); // 내용이 올바른지 확인
    //     assertEquals(request.getGender(), ootd.getGender()); // 성별이 올바른지 확인
    //     assertEquals(request.getIsPrivate(), ootd.isPrivate()); // 공개 여부가 올바른지 확인
    //
    //     // boardRepository.save 메서드가 한 번 호출되었는지 확인
    //     verify(ootdRepository, times(1)).save(ootd);
    // }
    //
    // @Test
    // @DisplayName("OOTD게시판_해당글_조회")
    // public void boardSelectSuccess() {
    //
    //     User user = new User();
    //     user.setId(1L);
    //
    //     OotdLike ootdLike = OotdLike.builder()
    //             .user(user)
    //             .build();
    //
    //     Ootd ootd = Ootd.builder()
    //             .contents("테스트 컨텐츠")
    //             .gender(UserGender.MALE)
    //             .build();
    //     ootd.addOotdLike(ootdLike);
    //
    //     ootd.setId(1L);
    //
    //     // boardRepository.findById 메서드의 결과로 샘플 Board 객체를 반환하도록 설정합니다.
    //     when(ootdRepository.findById(1L)).thenReturn(java.util.Optional.of(ootd));
    //
    //     // userService.getAuthenticatiedUser 메서드의 결과로 샘플 User 객체를 반환하도록 설정합니다.
    //     when(userService.getAuthenticatiedUser()).thenReturn(user);
    //
    //     OotdGetRes result = ootdService.getOotd(1L);
    //
    //     assertEquals("테스트 컨텐츠", result.getContents());
    //     assertEquals(UserGender.MALE, result.getGender());
    // }
}
