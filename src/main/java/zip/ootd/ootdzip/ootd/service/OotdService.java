package zip.ootd.ootdzip.ootd.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
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
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.ootd.data.OotdGetAllRes;
import zip.ootd.ootdzip.ootd.data.OotdGetByUserReq;
import zip.ootd.ootdzip.ootd.data.OotdGetByUserRes;
import zip.ootd.ootdzip.ootd.data.OotdGetOtherReq;
import zip.ootd.ootdzip.ootd.data.OotdGetOtherRes;
import zip.ootd.ootdzip.ootd.data.OotdGetRes;
import zip.ootd.ootdzip.ootd.data.OotdGetSimilarReq;
import zip.ootd.ootdzip.ootd.data.OotdGetSimilarRes;
import zip.ootd.ootdzip.ootd.data.OotdPatchReq;
import zip.ootd.ootdzip.ootd.data.OotdPostReq;
import zip.ootd.ootdzip.ootd.data.OotdPutReq;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimage.repository.OotdImageRepository;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;

@Service
@Transactional
@RequiredArgsConstructor
public class OotdService {

    private final OotdRepository ootdRepository;
    private final ClothesRepository clothesRepository;
    private final StyleRepository styleRepository;
    private final RedisDao redisDao;
    private final OotdImageRepository ootdImageRepository;

    public Ootd postOotd(OotdPostReq request, User loginUser) {

        List<OotdImage> ootdImages = request.getOotdImages().stream().map(ootdImage -> {
            List<OotdImageClothes> ootdImageClothesList = ootdImage.getClothesTags().stream().map(clothesTag -> {
                Clothes clothes = clothesRepository.findById(clothesTag.getClothesId()).orElseThrow();
                Coordinate coordinate = new Coordinate(clothesTag.getXRate(), clothesTag.getYRate());
                DeviceSize deviceSize = new DeviceSize(clothesTag.getDeviceWidth(), clothesTag.getDeviceHeight());

                return OotdImageClothes.createOotdImageClothesBy(clothes, coordinate, deviceSize);
            }).toList();

            return OotdImage.createOotdImageBy(ootdImage.getOotdImage(), ootdImageClothesList);
        }).toList();

        List<Style> styles = styleRepository.findAllById(request.getStyles());
        List<OotdStyle> ootdStyles = OotdStyle.createOotdStylesBy(styles);

        Ootd ootd = Ootd.createOotd(loginUser,
                request.getContent(),
                request.getIsPrivate(),
                ootdImages,
                ootdStyles);

        ootdRepository.save(ootd);
        return ootd;
    }

    public void updateContentsAndIsPrivate(Long id, OotdPatchReq request) {

        Ootd ootd = ootdRepository.findById(id).orElseThrow();

        ootd.updateIsPrivate(request.getIsPrivate());
    }

    public void updateAll(Long id, OotdPutReq request) {

        Ootd ootd = ootdRepository.findById(id).orElseThrow();

        List<OotdImage> ootdImages = request.getOotdImages().stream().map(ootdImage -> {
            List<OotdImageClothes> ootdImageClothesList = ootdImage.getClothesTags().stream().map(clothesTag -> {
                Clothes clothes = clothesRepository.findById(clothesTag.getClothesId()).orElseThrow();
                Coordinate coordinate = new Coordinate(clothesTag.getXRate(), clothesTag.getYRate());
                DeviceSize deviceSize = new DeviceSize(clothesTag.getDeviceWidth(), clothesTag.getDeviceHeight());

                return OotdImageClothes.createOotdImageClothesBy(clothes, coordinate, deviceSize);
            }).toList();

            return OotdImage.createOotdImageBy(ootdImage.getOotdImage(), ootdImageClothesList);
        }).toList();

        List<Style> styles = styleRepository.findAllById(request.getStyles());
        List<OotdStyle> ootdStyles = OotdStyle.createOotdStylesBy(styles);

        ootd.updateAll(request.getContent(),
                request.getIsPrivate(),
                ootdImages,
                ootdStyles);
    }

    public void deleteOotd(Long id) {
        Ootd ootd = ootdRepository.findById(id).orElseThrow();
        ootd.deleteOotd();
    }

    /**
     * 기본적인 단건조회 API 입니다.
     * 비공개글은 본인글이 아니면 볼 수 없습니다.
     */
    public OotdGetRes getOotd(Long ootdId, User loginUser) {

        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();

        checkOotd(ootd, loginUser);

        countViewInRedis(ootd, loginUser);
        int view = getView(ootd);
        int like = getLike(ootd);
        boolean isLike = getUserLike(ootd, loginUser);

        return new OotdGetRes(ootd, isLike, view, like, loginUser);
    }

    private void checkOotd(Ootd ootd, User user) {
        if (ootd.isPrivate() && !ootd.getWriter().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.PRIVATE);
        }
    }

    /**
     * 전체 ootd 조회
     * 삭제된글, 차단된글, 신고수가 특정 수 이상, 비공개글은 가져오지 않습니다.
     * 단, 비공개글이어도 본인 작성글이면 가져옵니다.
     */
    public SliceImpl<OotdGetAllRes> getOotds(User loginUser, int page) {

        int size = 20;
        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Slice<Ootd> ootds = ootdRepository.findAllByUserId(loginUser.getId(), pageable);

        List<OotdGetAllRes> ootdGetAllResList = ootds.stream()
                .map(ootd -> new OotdGetAllRes(
                        ootd,
                        getUserLike(ootd, loginUser),
                        getView(ootd),
                        getLike(ootd),
                        loginUser))
                .collect(Collectors.toList());

        return new SliceImpl<>(ootdGetAllResList, pageable, ootds.hasNext());
    }

    /**
     * ootdKey : ootd 게시글 키
     * ootdFilterKey : 중복된 사용자의 조회수 카운트 막기위한 ootd 필터키
     * updateKey : 추후 스케줄러작업에서 조회수가 변경된 게시판을 가져오기위해, ootdKey 를 저장해두는 키
     */
    private void countViewInRedis(Ootd ootd, User loginUser) {
        Long id = ootd.getId();

        if (!isUserViewedInRedis(id, loginUser)) {
            String ootdKey = RedisKey.VIEWS.makeKeyWith(id);
            String ootdFilterKey = RedisKey.VIEW_FILTER.makeKeyWith(id);
            String userKey = RedisKey.VIEWS.makeKeyWith(loginUser.getId());
            String updateKey = RedisKey.UPDATED_VIEWS.getKey();

            redisDao.setValuesSet(ootdFilterKey, userKey);
            redisDao.setValues(ootdKey, String.valueOf(getView(ootd) + 1));
            redisDao.setValuesSet(updateKey, ootdKey);
        }
    }

    private boolean isUserViewedInRedis(Long id, User loginUser) {
        String ootdFilterKey = RedisKey.VIEW_FILTER.makeKeyWith(id);
        String userKey = RedisKey.VIEWS.makeKeyWith(loginUser.getId());

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
    public void addLike(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();

        increaseLikeInRedis(ootd, loginUser);
        addUserLikeInRedis(ootdId, loginUser.getId());
        ootd.addLike(loginUser);
    }

    public void cancelLike(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();

        decreaseLikeInRedis(ootd, loginUser);
        cancelUserLikeInRedis(ootdId, loginUser.getId());
        ootd.cancelLike(loginUser);
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
            String updateKey = RedisKey.UPDATED_LIKES.getKey();
            redisDao.setValuesSet(updateKey, likeKey);
        }
    }

    private void decreaseLikeInRedis(Ootd ootd, User user) {
        Long id = ootd.getId();
        String likeKey = RedisKey.LIKES.makeKeyWith(id);

        if (getUserLike(ootd, user)) {
            redisDao.setValues(likeKey, String.valueOf(getLike(ootd) - 1));
            String updateKey = RedisKey.UPDATED_LIKES.getKey();
            redisDao.setValuesSet(updateKey, likeKey);
        }
    }

    /**
     * 로그인된 사용자 기준으로 북마크를 추가합니다.
     */
    public void addBookmark(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        ootd.addBookmark(loginUser);
    }

    public void cancelBookmark(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        ootd.cancelBookmark(loginUser);
    }

    /**
     * OOTD 상세 조회시, 현재 OOTD 작성자의 다른 OOTD 정보를 제공한다.
     */
    public CommonSliceResponse<OotdGetOtherRes> getOotdOther(OotdGetOtherReq request) {

        Long writerId = request.getUserId();
        Long ootdId = request.getOotdId();
        Pageable pageable = request.toPageable();

        Slice<Ootd> ootds = ootdRepository.findAllByUserIdAndOotdId(writerId, ootdId, pageable);

        List<OotdGetOtherRes> ootdGetOtherResList = ootds.stream()
                .map(OotdGetOtherRes::new)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(ootdGetOtherResList, pageable, ootds.isLast());
    }

    /**
     * OOTD 상세 조회시, 현재 OOTD 와 동일한 스타일의 다른 OOTD 를 제공합니다.
     */
    public CommonSliceResponse<OotdGetSimilarRes> getOotdSimilar(OotdGetSimilarReq request) {

        Long ootdId = request.getOotdId();
        Pageable pageable = request.toPageable();

        Ootd nowOotd = ootdRepository.findById(ootdId).orElseThrow();
        List<Style> styles = nowOotd.getStyles().stream()
                .map(OotdStyle::getStyle)
                .collect(Collectors.toList());

        Slice<OotdImage> ootdImages = ootdImageRepository.findByStyles(ootdId, styles, pageable);

        List<OotdGetSimilarRes> ootdGetSimilarResList = ootdImages.stream()
                .map(OotdGetSimilarRes::new)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(ootdGetSimilarResList, pageable, ootdImages.isLast());
    }

}
