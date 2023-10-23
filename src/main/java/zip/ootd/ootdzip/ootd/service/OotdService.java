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
                request.getIsPublic(),
                ootdImages,
                ootdClothesList,
                ootdStyles);

        ootdRepository.save(ootd);
        return ootd;
    }

    /**
     * 기본적인 단건조회 API 입니다.
     */
    public OotdGetRes getOotd(Long ootdId) {
        User user = userService.getAuthenticatiedUser();
        Ootd ootd = ootdRepository.findOotd(ootdId).orElseThrow();

        countViewInRedis(ootd);
        int view = getView(ootd);
        int like = getLike(ootd);
        boolean isLike = getUserLike(ootd, user);
        boolean isBookmark = ootd.isBookmark(user);

        return new OotdGetRes(ootd, isLike, isBookmark, view, like);
    }

    /**
     * 본인글을 단건 조회할 때 사용되는 로직입니다.
     * 해당 로직에서는 isPublic 값과 상관없이 가져오기에 유저가 선택한 공개/비공개 상관없이 조회됩니다.
     */
    public OotdGetRes getOotdInMine(Long ootdId) {
        User user = userService.getAuthenticatiedUser();
        Ootd ootd = ootdRepository.findOotdRegardlessOfIsPublic(ootdId).orElseThrow();

        countViewInRedis(ootd);
        int view = getView(ootd);
        int like = getLike(ootd);
        boolean isLike = getUserLike(ootd, user);
        boolean isBookmark = ootd.isBookmark(user);

        return new OotdGetRes(ootd, isLike, isBookmark, view, like);
    }

    public List<OotdGetAllRes> getOotds() {
        List<Ootd> ootds = ootdRepository.findOotdAll();
        User user = userService.getAuthenticatiedUser();

        return ootds.stream()
                .map(b -> new OotdGetAllRes(b, getUserLike(b, user), b.isBookmark(user), getView(b), getLike(b)))
                .collect(Collectors.toList());
    }

    private void countViewInRedis(Ootd ootd) {
        Long id = ootd.getId();

        if (!isUserViewedInRedis(id)) {
            String ootdKey = RedisKey.VIEW.makeKeyWith(id);
            String ootdFilterKey = RedisKey.VIEW.makeFilterKeyWith(id);
            String userKey = RedisKey.VIEW.makeKeyWith(userService.getAuthenticatiedUser().getId());

            redisDao.setValuesSet(ootdFilterKey, userKey);
            redisDao.setValues(ootdKey, String.valueOf(getView(ootd) + 1));
        }
    }

    private boolean isUserViewedInRedis(Long id) {
        String ootdFilterKey = RedisKey.VIEW.makeFilterKeyWith(id);
        String userKey = RedisKey.VIEW.makeKeyWith(userService.getAuthenticatiedUser().getId());

        return redisDao.getValuesSet(ootdFilterKey).contains(userKey);
    }

    private int getViewInRedis(Long id) {
        String ootdKey = RedisKey.VIEW.makeKeyWith(id);

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
        String ootdKey = RedisKey.VIEW.makeKeyWith(id);

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
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        return NumberUtils.toInt(redisDao.getValues(likeKey));
    }

    private void setLikeInRedis(Long id, int count) {
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        redisDao.setValues(likeKey, String.valueOf(count));
    }

    public void addLike(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long userId = user.getId();

        countLikeInRedis(ootd, user);
        addUserLikeInRedis(ootdId, userId);
        ootd.addLike(user);
    }

    public void cancelLike(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        User user = userService.getAuthenticatiedUser();
        Long userId = user.getId();

        discountLikeInRedis(ootd, user);
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
        String ootdKey = RedisKey.USER_LIKE.makeKeyWith(ootdId);

        return redisDao.getValuesSet(ootdKey).size() != 0;
    }

    private boolean isUserLikeInRedis(Long ootdId, Long userId) {
        String ootdKey = RedisKey.USER_LIKE.makeKeyWith(ootdId);
        String userKey = RedisKey.USER_LIKE.makeKeyWith(userId);

        return redisDao.getValuesSet(ootdKey).contains(userKey);
    }

    private void addUserLikeInRedis(Long ootdId, Long userId) {
        String ootdKey = RedisKey.USER_LIKE.makeKeyWith(ootdId);
        String userKey = RedisKey.USER_LIKE.makeKeyWith(userId);

        redisDao.setValuesSet(ootdKey, userKey);
    }

    private void cancelUserLikeInRedis(Long ootdId, Long userId) {
        String ootdKey = RedisKey.USER_LIKE.makeKeyWith(ootdId);
        String userKey = RedisKey.USER_LIKE.makeKeyWith(userId);

        redisDao.deleteValuesSet(ootdKey, userKey);
    }

    private void countLikeInRedis(Ootd ootd, User user) {
        Long id = ootd.getId();
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        if (!getUserLike(ootd, user)) {
            redisDao.setValues(likeKey, String.valueOf(getLike(ootd) + 1));
        }
    }

    private void discountLikeInRedis(Ootd ootd, User user) {
        Long id = ootd.getId();
        String likeKey = RedisKey.LIKE.makeKeyWith(id);

        if (getUserLike(ootd, user)) {
            redisDao.setValues(likeKey, String.valueOf(getLike(ootd) - 1));
        }
    }

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
