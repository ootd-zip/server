package zip.ootd.ootdzip.board.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import zip.ootd.ootdzip.board.data.*;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.board.domain.BoardImage;
import zip.ootd.ootdzip.board.repository.BoardRepository;
import zip.ootd.ootdzip.boardclothe.domain.BoardClothes;
import zip.ootd.ootdzip.boardstyle.BoardStyle;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final ClothesRepository clothesRepository;
    private final UserService userService;
    private final StyleRepository styleRepository;
    private final RedisDao redisDao;

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

    public BoardOotdGetRes getOotd(BoardOotdGetReq request) {

        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();

        Long boardId = board.getId();
        countView(boardId);
        int view = getViewInRedis(boardId);
        int like = getLikeInRedis(boardId);
        boolean isLike = board.isUserLike(user.getId());

        return new BoardOotdGetRes(board, isLike, view, like);
    }

    public List<BoardOotdGetAllRes> getOotds() {

        List<Board> boards = boardRepository.findOotdAll();
        User user = userService.getAuthenticatiedUser();

        return boards.stream()
                .map(b -> new BoardOotdGetAllRes(b, b.isUserLike(user.getId()), getViewInRedis(b.getId()), getLikeInRedis(b.getId())))
                .collect(Collectors.toList());
    }


    public void countView(Long id) {

        String info = "view";
        String filter = "filter";
        String redisKey = id.toString() + info;
        String userKey = userService.getAuthenticatiedUser().getId().toString() + info + filter;

        if (!redisDao.getValuesList(userKey).contains(redisKey)) {
            redisDao.setValuesList(userKey, redisKey);
            int view = NumberUtils.toInt(redisDao.getValues(redisKey));
            redisDao.setValues(redisKey, String.valueOf(view + 1));
        }
    }

    public int getViewInRedis(Long id) {

        String info = "view";
        String redisKey = id.toString() + info;

        return NumberUtils.toInt(redisDao.getValues(redisKey));
    }

    public int getView(Board board) {
        return Math.max(getViewInRedis(board.getId()), board.getViewCount());
    }

    public boolean changeLike(BoardLikeReq request) {

        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        return board.changeUserLike(userService.getAuthenticatiedUser().getId());
    }

    private void countLike(Long id) {

        String info = "like";
        String filter = "filter";
        String redisKey = id.toString() + info;
        String userKey = userService.getAuthenticatiedUser().getId().toString() + info + filter;

        if (!redisDao.getValuesList(userKey).contains(redisKey)) {
            redisDao.setValuesList(userKey, redisKey);
            int like = NumberUtils.toInt(redisDao.getValues(redisKey));
            redisDao.setValues(redisKey, String.valueOf(like + 1));
        }
    }

    public int getLike(Board board) {
        return Math.max(getLikeInRedis(board.getId()), board.getLikeCount());
    }

    private int getLikeInRedis(Long id) {

        String info = "like";
        String redisKey = id.toString() + info;

        return NumberUtils.toInt(redisDao.getValues(redisKey));
    }
}
