package zip.ootd.ootdzip.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.board.data.BoardAddBookmarkReq;
import zip.ootd.ootdzip.board.data.BoardCancelBookmarkReq;
import zip.ootd.ootdzip.board.data.BoardLikeReq;
import zip.ootd.ootdzip.board.data.BoardOotdGetAllRes;
import zip.ootd.ootdzip.board.data.BoardOotdGetReq;
import zip.ootd.ootdzip.board.data.BoardOotdGetRes;
import zip.ootd.ootdzip.board.data.BoardOotdPostReq;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.board.domain.BoardImage;
import zip.ootd.ootdzip.board.repository.BoardRepository;
import zip.ootd.ootdzip.boardbookmark.repository.BoardBookmarkRepository;
import zip.ootd.ootdzip.boardclothe.domain.BoardClothes;
import zip.ootd.ootdzip.boardstyle.BoardStyle;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final ClothesRepository clothesRepository;
    private final UserService userService;
    private final StyleRepository styleRepository;
    private final RedisDao redisDao;
    private final BoardBookmarkRepository boardBookmarkRepository;

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

        countView(board);
        int view = getView(board);
        int like = getLike(board);
        boolean isLike = isUserLike(board, user);
        boolean isBookmark = board.isBookmark(user);

        return new BoardOotdGetRes(board, isLike, isBookmark, view, like);
    }

    public List<BoardOotdGetAllRes> getOotds() {

        List<Board> boards = boardRepository.findOotdAll();
        User user = userService.getAuthenticatiedUser();

        return boards.stream()
                .map(b -> new BoardOotdGetAllRes(b, isUserLike(b, user), b.isBookmark(user), getView(b), getLike(b)))
                .collect(Collectors.toList());
    }

    private void countView(Board board) {

        Long id = board.getId();

        if (isCountViewInRedis(id)) {
            countViewInRedis(id, 0);
        }

        int view = board.getViewCount();
        countViewInRedis(id, view);
    }

    private void countViewInRedis(Long id, int boardView) {

        String info = "view";
        String filter = "filter";
        String boardKey = id.toString() + info;
        String boardFilterKey = boardKey + filter;
        String userKey = userService.getAuthenticatiedUser().getId().toString() + info;

        if (!redisDao.getValuesSet(boardFilterKey).contains(userKey)) {
            redisDao.setValuesSet(boardFilterKey, userKey);
            int view = NumberUtils.toInt(redisDao.getValues(boardKey));
            redisDao.setValues(boardKey, String.valueOf(boardView + view + 1));
        }
    }

    private void setViewInRedis(Long id, int count) {
        String info = "view";
        String boardKey = id.toString() + info;

        redisDao.setValues(boardKey, String.valueOf(count));
    }

    private boolean isCountViewInRedis(Long id) {
        String info = "view";
        String filter = "filter";
        String boardFilterKey = id.toString() + info + filter;
        String userKey = userService.getAuthenticatiedUser().getId().toString() + info;

        return redisDao.getValuesSet(boardFilterKey).contains(userKey);
    }

    private int getViewInRedis(Long id) {

        String info = "view";
        String redisKey = id.toString() + info;

        return NumberUtils.toInt(redisDao.getValues(redisKey));
    }

    // getView 의 경우 DB에서 가져온 view 와 redis 에서 가져온 view 를 비교 하여 redis 가 비어있다면 redis 최신화 후 db의 값을 반환
    private int getView(Board board) {
        int viewInRedis = getViewInRedis(board.getId());
        int viewInDb = board.getViewCount();

        if (viewInRedis > 0) {
            return viewInRedis;
        }

        setViewInRedis(board.getId(), viewInDb);
        return viewInDb;
    }

    public boolean changeLike(BoardLikeReq request) {

        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long boardId = board.getId();
        Long userId = user.getId();

        boolean resultLike = board.changeUserLike(user);
        changeUserLikeInRedis(boardId, userId, resultLike);

        return resultLike;
    }

    private boolean isUserLike(Board board, User user) {

        Long boardId = board.getId();
        Long userId = user.getId();

        if (isUserLikeSavedRedis(boardId)) {
            return isUserLikeInRedis(boardId, userId);
        }

        return board.isUserLike(userId);
    }

    private boolean isUserLikeSavedRedis(Long boardId) {
        String info = "userLike";
        String boardKey = boardId + info;

        return redisDao.getValuesSet(boardKey).size() != 0;
    }

    private boolean isUserLikeInRedis(Long boardId, Long userId) {
        String info = "userLike";
        String boardKey = boardId + info;
        String userKey = userId + info;

        return redisDao.getValuesSet(boardKey).contains(userKey);
    }

    // 해당 함수는 목표하는 like 에 맞춰서 redis 값을 조정하는데 쓰입니다.
    // 예를 들어 targetLike 가 true 이면 false(싫어요) -> true(좋아요) 로 바뀐것이므로 redis 에서 좋아요수를 증가시킵니다.
    private boolean changeUserLikeInRedis(Long boardId, Long userId, boolean targetLike) {
        String info = "userLike";
        String boardKey = boardId + info;
        String userKey = userId + info;

        if (targetLike) {
            redisDao.setValuesSet(boardKey, userKey);
            countLikeInRedis(boardId);
        } else {
            redisDao.deleteValuesSet(boardKey, userKey);
            discountLikeInRedis(boardId);
        }

        return targetLike;
    }

    private void countLikeInRedis(Long id) {

        String info = "like";
        String likeKey = id.toString() + info;

        int like = NumberUtils.toInt(redisDao.getValues(likeKey));
        redisDao.setValues(likeKey, String.valueOf(like + 1));
    }

    private void discountLikeInRedis(Long id) {

        String info = "like";
        String likeKey = id.toString() + info;

        int like = NumberUtils.toInt(redisDao.getValues(likeKey));
        redisDao.setValues(likeKey, String.valueOf(like - 1));
    }

    private void setLikeInRedis(Long id, int count) {

        String info = "like";
        String likeKey = id.toString() + info;

        redisDao.setValues(likeKey, String.valueOf(count));
    }

    // getLike 의 경우 DB에서 가져온 like 와 redis 에서 가져온 like 를 비교 하여 redis 가 비어있다면 redis 최신화 후 db의 값을 반환
    private int getLike(Board board) {
        int likeInRedis = getLikeInRedis(board.getId());
        int likeInDb = board.getLikeCount();
        if (likeInRedis > 0) {
            return likeInRedis;
        }
        setLikeInRedis(board.getId(), likeInDb);
        return likeInDb;
    }

    private int getLikeInRedis(Long id) {

        String info = "like";
        String likeKey = id.toString() + info;

        return NumberUtils.toInt(redisDao.getValues(likeKey));
    }

    public void addBookmark(BoardAddBookmarkReq request) {
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        board.addBookmark(user);
    }

    public void cancelBookmark(BoardCancelBookmarkReq request) {
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        board.cancelBookmark(user);
    }
}
