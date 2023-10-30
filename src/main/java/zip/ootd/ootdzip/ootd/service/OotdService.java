package zip.ootd.ootdzip.ootd.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.constant.RedisKey;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.ootd.data.OotdGetAllRes;
import zip.ootd.ootdzip.ootd.data.OotdGetRes;
import zip.ootd.ootdzip.ootd.data.OotdPostReq;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.domain.OotdImage;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdclothe.domain.OotdClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class OotdService {

    private final OotdRepository ootdRepository;
    private final ClothesRepository clothesRepository;
    private final UserService userService;
    private final StyleRepository styleRepository;
    private final RedisDao redisDao;

    public Ootd postOotd(OotdPostReq request) {

        List<String> images = request.getOotdImages();
        List<Long> clothesIds = request.getClotheIds();
        List<Clothes> clothesList = clothesRepository.findAllById(clothesIds);
        List<Style> styles = styleRepository.findAllById(request.getStyles());

        List<OotdImage> ootdImages = OotdImage.createOotdImagesBy(images);
        List<OotdClothes> ootdClothesList = OotdClothes.createOotdClothesListBy(clothesList);
        List<OotdStyle> ootdStyles = OotdStyle.createOotdStylesBy(styles);

        Ootd ootd = Ootd.createOotd(userService.getAuthenticatiedUser(),
                request.getContent(),
                request.getGender(),
                request.getIsPrivate(),
                ootdImages,
                ootdClothesList,
                ootdStyles);

        ootdRepository.save(ootd);
        return ootd;
    }

    /**
     * 기본적인 단건조회 API 입니다.
     * 비공개글은 본인글이 아니면 볼 수 없습니다.
     */
    public OotdGetRes getOotd(Long ootdId) {
        User user = userService.getAuthenticatiedUser();
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();

        checkOotd(ootd, user);

        countViewInRedis(ootd);
        int view = getView(ootd);
        int like = getLike(ootd);
        boolean isLike = getUserLike(ootd, user);
        boolean isBookmark = ootd.isBookmark(user);

        return new OotdGetRes(ootd, isLike, isBookmark, view, like);
    }

    private void checkOotd(Ootd ootd, User user) {
        if (ootd.isPrivate() && !ootd.getWriter().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.PRIVATE);
        }

        if (ootd.getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED);
        }

        if (ootd.getIsBlocked()) {
            throw new CustomException(ErrorCode.BLOCKED);
        }

        if (ootd.getReportCount() >= 10) {
            throw new CustomException(ErrorCode.OVER_REPORT);
        }
    }

    /**
     * 전체 ootd 조회
     * 삭제된글, 차단된글, 신고수가 특정 수 이상, 비공개글은 가져오지 않습니다.
     * 단, 비공개글이어도 본인 작성글이면 가져옵니다.
     */
    public List<OotdGetAllRes> getOotds() {
        User user = userService.getAuthenticatiedUser();
        List<Ootd> ootds = ootdRepository.findAllByUserId(user.getId());

        return ootds.stream()
                .map(b -> new OotdGetAllRes(b, getUserLike(b, user), b.isBookmark(user), getView(b), getLike(b)))
                .collect(Collectors.toList());
    }

    /**
     * ootdKey : ootd 게시글 키
     * ootdFilterKey : 중복된 사용자의 조회수 카운트 막기위한 ootd 필터키
     * updateKey : 추후 스케줄러작업에서 조회수가 변경된 게시판을 가져오기위해, ootdKey 를 저장해두는 키
     */
    private void countViewInRedis(Ootd ootd) {
        Long id = ootd.getId();

        if (!isUserViewedInRedis(id)) {
            String ootdKey = RedisKey.VIEWS.makeKeyWith(id);
            String ootdFilterKey = RedisKey.VIEWS.makeFilterKeyWith(id);
            String userKey = RedisKey.VIEWS.makeKeyWith(userService.getAuthenticatiedUser().getId());
            String updateKey = RedisKey.UPDATED_VIEWS.getKey();

            redisDao.setValuesSet(ootdFilterKey, userKey);
            redisDao.setValues(ootdKey, String.valueOf(getView(ootd) + 1));
            redisDao.setValuesSet(updateKey, ootdKey);
        }
    }

    private boolean isUserViewedInRedis(Long id) {
        String ootdFilterKey = RedisKey.VIEWS.makeFilterKeyWith(id);
        String userKey = RedisKey.VIEWS.makeKeyWith(userService.getAuthenticatiedUser().getId());

        return redisDao.getValuesSet(ootdFilterKey).contains(userKey);
    }

    private int getViewInRedis(Long id) {
        String ootdKey = RedisKey.VIEWS.makeKeyWith(id);

        return NumberUtils.toInt(redisDao.getValues(ootdKey));
    }

    /**
     * redis 에 조회수가 저장되어있으면 반환, 없으면 entity 에서 조회수를 가져와 redis에 저장 후 조회수 반환
     */
    private int getView(Ootd ootd) {
        int viewInRedis = getViewInRedis(ootd.getId());

        if (viewInRedis > 0) {
            return viewInRedis;
        }

        int viewInDb = ootd.getViewCount();
        setViewInRedis(ootd.getId(), viewInDb);
        return viewInDb;
    }

    private void setViewInRedis(Long id, int count) {
        String ootdKey = RedisKey.VIEWS.makeKeyWith(id);

        redisDao.setValues(ootdKey, String.valueOf(count));
    }

    /**
     * redis 에 좋아요수가 저장되어있으면 반환, 없으면 entity 에서 좋아요수를 가져와 redis에 저장 후 좋아요 수 반환
     */
    private int getLike(Ootd ootd) {
        int likeInRedis = getLikeInRedis(ootd.getId());

        if (likeInRedis > 0) {
            return likeInRedis;
        }

        int likeInDb = ootd.getLikeCount();
        setLikeInRedis(ootd.getId(), likeInDb);
        return likeInDb;
    }

    private int getLikeInRedis(Long id) {
        String likeKey = RedisKey.LIKES.makeKeyWith(id);

        return NumberUtils.toInt(redisDao.getValues(likeKey));
    }

    private void setLikeInRedis(Long id, int count) {
        String likeKey = RedisKey.LIKES.makeKeyWith(id);

        redisDao.setValues(likeKey, String.valueOf(count));
    }

    /**
     * 로그인된 사용자 기준으로 좋아요를 추가합니다.
     */
    public void addLike(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long userId = user.getId();

        increaseLikeInRedis(ootd, user);
        addUserLikeInRedis(ootdId, userId);
        ootd.addLike(user);
    }

    public void cancelLike(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long userId = user.getId();

        decreaseLikeInRedis(ootd, user);
        cancelUserLikeInRedis(ootdId, userId);
        ootd.cancelLike(user);
    }

    /**
     * redis 에 유저가 좋아요를 한 기록이 저장되어있으면 반환, 없으면 entity 에서 유저의 좋아요 여부 가져와 redis에 저장 후 좋아요 여부 반환
     */
    private boolean getUserLike(Ootd ootd, User user) {

        Long ootdId = ootd.getId();
        Long userId = user.getId();

        if (isUserLikeSavedInRedis(ootdId)) {
            return isUserLikeInRedis(ootdId, userId);
        }

        boolean userLike = ootd.isOotdLike(user);
        if (userLike) {
            addUserLikeInRedis(ootdId, userId);
        }

        return userLike;
    }

    private boolean isUserLikeSavedInRedis(Long ootdId) {
        String ootdKey = RedisKey.USER_LIKES.makeKeyWith(ootdId);

        return redisDao.getValuesSet(ootdKey).size() != 0;
    }

    private boolean isUserLikeInRedis(Long ootdId, Long userId) {
        String ootdKey = RedisKey.USER_LIKES.makeKeyWith(ootdId);
        String userKey = RedisKey.USER_LIKES.makeKeyWith(userId);

        return redisDao.getValuesSet(ootdKey).contains(userKey);
    }

    private void addUserLikeInRedis(Long ootdId, Long userId) {
        String ootdKey = RedisKey.USER_LIKES.makeKeyWith(ootdId);
        String userKey = RedisKey.USER_LIKES.makeKeyWith(userId);

        redisDao.setValuesSet(ootdKey, userKey);
    }

    private void cancelUserLikeInRedis(Long ootdId, Long userId) {
        String ootdKey = RedisKey.USER_LIKES.makeKeyWith(ootdId);
        String userKey = RedisKey.USER_LIKES.makeKeyWith(userId);

        redisDao.deleteValuesSet(ootdKey, userKey);
    }

    private void increaseLikeInRedis(Ootd ootd, User user) {
        Long id = ootd.getId();
        String likeKey = RedisKey.LIKES.makeKeyWith(id);

        if (!getUserLike(ootd, user)) {
            redisDao.setValues(likeKey, String.valueOf(getLike(ootd) + 1));
        }
    }

    private void decreaseLikeInRedis(Ootd ootd, User user) {
        Long id = ootd.getId();
        String likeKey = RedisKey.LIKES.makeKeyWith(id);

        if (getUserLike(ootd, user)) {
            redisDao.setValues(likeKey, String.valueOf(getLike(ootd) - 1));
        }
    }

    /**
     * 로그인된 사용자 기준으로 북마크를 추가합니다.
     */
    public void addBookmark(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        ootd.addBookmark(user);
    }

    public void cancelBookmark(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        ootd.cancelBookmark(user);
    }
}
