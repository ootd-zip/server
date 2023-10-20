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
        boolean isLike = getUserLike(board, user);
        boolean isBookmark = board.isBookmark(user);

        return new BoardOotdGetRes(board, isLike, isBookmark, view, like);
    }

    public List<BoardOotdGetAllRes> getOotds() {
        List<Board> boards = boardRepository.findOotdAll();
        User user = userService.getAuthenticatiedUser();

        return boards.stream()
                .map(b -> new BoardOotdGetAllRes(b, getUserLike(b, user), b.isBookmark(user), getView(b), getLike(b)))
                .collect(Collectors.toList());
    }

    private void countViewInRedis(Board board) {
        Long id = board.getId();

        if (!isUserViewedInRedis(id)) {
            String boardKey = RedisKey.VIEW.makeKeyWith(id);
            String boardFilterKey = RedisKey.VIEW.makeFilterKeyWith(id);
            String userKey = RedisKey.VIEW.makeKeyWith(userService.getAuthenticatiedUser().getId());

            redisDao.setValuesSet(boardFilterKey, userKey);
            redisDao.setValues(boardKey, String.valueOf(getView(board) + 1));
        }
    }

    private boolean isUserViewedInRedis(Long id) {
        String boardFilterKey = RedisKey.VIEW.makeFilterKeyWith(id);
        String userKey = RedisKey.VIEW.makeKeyWith(userService.getAuthenticatiedUser().getId());

        return redisDao.getValuesSet(boardFilterKey).contains(userKey);
    }

    private int getViewInRedis(Long id) {
        String boardKey = RedisKey.VIEW.makeKeyWith(id);

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
        String boardKey = RedisKey.VIEW.makeKeyWith(id);

        redisDao.setValues(boardKey, String.valueOf(count));
    }

    private int getLike(Board board) {
        int likeInRedis = getLikeInRedis(board.getId());

        if (likeInRedis > 0) {
            return likeInRedis;
        }

        int likeInDb = board.getLikeCount();
        setLikeInRedis(board.getId(), likeInDb);
        return likeInDb;
    }

    private int getLikeInRedis(Long id) {
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        return NumberUtils.toInt(redisDao.getValues(likeKey));
    }

    private void setLikeInRedis(Long id, int count) {
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        redisDao.setValues(likeKey, String.valueOf(count));
    }

    public void addLike(BoardAddLikeReq request) {
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long boardId = board.getId();
        Long userId = user.getId();

        countLikeInRedis(board, user);
        addUserLikeInRedis(boardId, userId);
        board.addLike(user);
    }

    public void cancelLike(BoardCancelLikeReq request) {
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long boardId = board.getId();
        Long userId = user.getId();

        discountLikeInRedis(board, user);
        cancelUserLikeInRedis(boardId, userId);
        board.cancelLike(user);
    }

    private boolean getUserLike(Board board, User user) {

        Long boardId = board.getId();
        Long userId = user.getId();

        if (isUserLikeSavedRedis(boardId)) {
            return isUserLikeInRedis(boardId, userId);
        }

        boolean userLike = board.isBoardLike(user);
        if (userLike) {
            addUserLikeInRedis(boardId, userId);
        }

        return userLike;
    }

    private boolean isUserLikeSavedRedis(Long boardId) {
        String boardKey = RedisKey.USER_LIKE.makeKeyWith(boardId);

        return redisDao.getValuesSet(boardKey).size() != 0;
    }

    private boolean isUserLikeInRedis(Long boardId, Long userId) {
        String boardKey = RedisKey.USER_LIKE.makeKeyWith(boardId);
        String userKey = RedisKey.USER_LIKE.makeKeyWith(userId);

        return redisDao.getValuesSet(boardKey).contains(userKey);
    }

    private void addUserLikeInRedis(Long boardId, Long userId) {
        String boardKey = RedisKey.USER_LIKE.makeKeyWith(boardId);
        String userKey = RedisKey.USER_LIKE.makeKeyWith(userId);

        redisDao.setValuesSet(boardKey, userKey);
    }

    private void cancelUserLikeInRedis(Long boardId, Long userId) {
        String boardKey = RedisKey.USER_LIKE.makeKeyWith(boardId);
        String userKey = RedisKey.USER_LIKE.makeKeyWith(userId);

        redisDao.deleteValuesSet(boardKey, userKey);
    }

    private void countLikeInRedis(Board board, User user) {
        Long id = board.getId();
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        if (!getUserLike(board, user)) {
            redisDao.setValues(likeKey, String.valueOf(getLike(board) + 1));
        }
    }

    private void discountLikeInRedis(Board board, User user) {
        Long id = board.getId();
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        if (getUserLike(board, user)) {
            redisDao.setValues(likeKey, String.valueOf(getLike(board) - 1));
        }
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
