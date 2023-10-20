package zip.ootd.ootdzip.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.board.data.BoardAddBookmarkReq;
import zip.ootd.ootdzip.board.data.BoardAddLikeReq;
import zip.ootd.ootdzip.board.data.BoardCancelBookmarkReq;
import zip.ootd.ootdzip.board.data.BoardCancelLikeReq;
import zip.ootd.ootdzip.board.data.BoardOotdGetAllRes;
import zip.ootd.ootdzip.board.data.BoardOotdGetReq;
import zip.ootd.ootdzip.board.data.BoardOotdGetRes;
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
import zip.ootd.ootdzip.common.constant.RedisKey;
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

        countViewInRedis(board);
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

    private void countViewInRedis(Board board) {
        Long id = board.getId();

        if (!isUserViewedInRedis(id)) {
            String boardKey = id + RedisKey.View.KEY;
            String boardFilterKey = id + RedisKey.View.FILTER_KEY;
            String userKey = userService.getAuthenticatiedUser().getId().toString() + RedisKey.View.KEY;

            redisDao.setValuesSet(boardFilterKey, userKey);
            redisDao.setValues(boardKey, String.valueOf(getView(board) + 1));
        }
    }

    private boolean isUserViewedInRedis(Long id) {
        String boardFilterKey = id + RedisKey.View.FILTER_KEY;
        String userKey = userService.getAuthenticatiedUser().getId().toString() + RedisKey.View.KEY;

        return redisDao.getValuesSet(boardFilterKey).contains(userKey);
    }

    private int getViewInRedis(Long id) {
        String boardKey = id + RedisKey.View.KEY;

        return NumberUtils.toInt(redisDao.getValues(boardKey));
    }

    private int getView(Board board) {
        int viewInRedis = getViewInRedis(board.getId());

        if (viewInRedis > 0) {
            return viewInRedis;
        }

        int viewInDb = board.getViewCount();
        setViewInRedis(board.getId(), viewInDb);
        return viewInDb;
    }

    private void setViewInRedis(Long id, int count) {
        String boardKey = id + RedisKey.View.KEY;

        redisDao.setValues(boardKey, String.valueOf(count));
    }

    public void addLike(BoardAddLikeReq request) {
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long boardId = board.getId();
        Long userId = user.getId();

        board.addLike(user);
        addUserLikeInRedis(boardId, userId);
    }

    public void cancelLike(BoardCancelLikeReq request) {
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long boardId = board.getId();
        Long userId = user.getId();

        board.cancelLike(user);
        cancelUserLikeInRedis(boardId, userId);
    }

    private boolean isUserLike(Board board, User user) {

        Long boardId = board.getId();
        Long userId = user.getId();

        if (isUserLikeSavedRedis(boardId)) {
            return isUserLikeInRedis(boardId, userId);
        }

        return board.isBoardLike(user);
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

    private void addUserLikeInRedis(Long boardId, Long userId) {
        String info = "userLike";
        String boardKey = boardId + info;
        String userKey = userId + info;

        redisDao.setValuesSet(boardKey, userKey);
        countLikeInRedis(boardId);
    }

    private void cancelUserLikeInRedis(Long boardId, Long userId) {
        String info = "userLike";
        String boardKey = boardId + info;
        String userKey = userId + info;

        redisDao.deleteValuesSet(boardKey, userKey);
        discountLikeInRedis(boardId);
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
