package zip.ootd.ootdzip.board.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import zip.ootd.ootdzip.board.data.BoardOotdPostReq;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.board.repository.BoardRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;
import zip.ootd.ootdzip.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ClothesRepository clothesRepository;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("OOTD게시판_정상_등록")
    public void boardRegisterSuccess() {
        // 가짜 데이터 생성
        BoardOotdPostReq request = new BoardOotdPostReq();
        request.setOotdImages(new ArrayList<>()); // 필요한 이미지 URL 리스트 추가
        request.setClotheIds(new ArrayList<>()); // 필요한 의류 ID 리스트 추가
        request.setContent("OOTD Content");
        request.setGender(UserGender.MALE); // 적절한 성별 설정
        request.setStyles(new ArrayList<>());
        request.setIsPublic(true);

        // Mock 객체의 동작 설정
        List<Clothes> clothesList = new ArrayList<>();
        when(clothesRepository.findAllById(request.getClotheIds())).thenReturn(clothesList);

        User user = new User();
        when(userService.getAuthenticatiedUser()).thenReturn(user);

        // Board 객체 생성
        Board board = boardService.postOotd(request);

        // Board 객체가 잘 생성되었는지 검증
        assertNotNull(board); // Board 객체가 null이 아닌지 확인
        assertEquals(user, board.getWriter()); // 작성자가 올바른지 확인
        assertEquals(request.getContent(), board.getContents()); // 내용이 올바른지 확인
        assertEquals(request.getGender(), board.getGender()); // 성별이 올바른지 확인
        assertEquals(request.getIsPublic(), board.isPublic()); // 공개 여부가 올바른지 확인

        // boardRepository.save 메서드가 한 번 호출되었는지 확인
        verify(boardRepository, times(1)).save(board);
    }
}
