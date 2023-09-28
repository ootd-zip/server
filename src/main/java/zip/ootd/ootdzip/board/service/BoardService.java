package zip.ootd.ootdzip.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.board.data.BoardOotdPostReq;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.board.domain.BoardImage;
import zip.ootd.ootdzip.board.repository.BoardRepository;
import zip.ootd.ootdzip.boardclothe.domain.BoardClothes;
import zip.ootd.ootdzip.boardstyle.BoardStyle;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.user.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final ClothesRepository clothesRepository;
    private final StyleRepository styleRepository;
    private final UserService userService;

    public Board postOotd(BoardOotdPostReq request) {

        List<String> ootdImages = request.getOotdImages();
        List<Long> clothesIds = request.getClotheIds();
        List<Clothes> clothesList = clothesRepository.findAllById(clothesIds);
        List<Style> styles = styleRepository.findAllById(request.getStyles());

        List<BoardImage> boardImages = BoardImage.createBoardImagesBy(ootdImages);
        List<BoardClothes> boardClothesList = BoardClothes.createBoardClothesListBy(clothesList);
        List<BoardStyle> boardStyles = BoardStyle.createBoardStylesBy(styles);

        Board board = Board.createBoard(userService.getAuthenticatiedUser(),
                request.getContent(),
                request.getGender(),
                request.getIsPublic(),
                boardImages,
                boardClothesList,
                boardStyles);

        boardRepository.save(board);
        return board;
    }
}
