package zip.ootd.ootdzip.ootd.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.constant.RedisKey;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.images.domain.Images;
import zip.ootd.ootdzip.images.service.ImagesService;
import zip.ootd.ootdzip.lock.annotation.RLockCustom;
import zip.ootd.ootdzip.lock.domain.RLockType;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.event.NotificationEvent;
import zip.ootd.ootdzip.ootd.controller.response.OotdSearchRes;
import zip.ootd.ootdzip.ootd.data.OotdGetAllRes;
import zip.ootd.ootdzip.ootd.data.OotdGetByUserReq;
import zip.ootd.ootdzip.ootd.data.OotdGetByUserRes;
import zip.ootd.ootdzip.ootd.data.OotdGetClothesReq;
import zip.ootd.ootdzip.ootd.data.OotdGetClothesRes;
import zip.ootd.ootdzip.ootd.data.OotdGetOtherReq;
import zip.ootd.ootdzip.ootd.data.OotdGetOtherRes;
import zip.ootd.ootdzip.ootd.data.OotdGetRes;
import zip.ootd.ootdzip.ootd.data.OotdGetSimilarReq;
import zip.ootd.ootdzip.ootd.data.OotdGetSimilarRes;
import zip.ootd.ootdzip.ootd.data.OotdPatchReq;
import zip.ootd.ootdzip.ootd.data.OotdPostReq;
import zip.ootd.ootdzip.ootd.data.OotdPutReq;
import zip.ootd.ootdzip.ootd.data.OotdTodayReq;
import zip.ootd.ootdzip.ootd.data.OotdTodayRes;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootd.service.request.OotdSearchSvcReq;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimage.repository.OotdImageRepository;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;
import zip.ootd.ootdzip.weather.data.Temperatures;
import zip.ootd.ootdzip.weather.service.WeatherService;

@Service
@RequiredArgsConstructor
public class OotdService {

    private final OotdRepository ootdRepository;
    private final UserService userService;
    private final ClothesRepository clothesRepository;
    private final StyleRepository styleRepository;
    private final RedisDao redisDao;
    private final OotdImageRepository ootdImageRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserBlockRepository userBlockRepository;
    private final ImagesService imagesService;
    private final WeatherService weatherService;

    @Transactional
    public Ootd postOotd(OotdPostReq request, User loginUser) {

        List<OotdImage> ootdImages = request.getOotdImages().stream().map(ootdImage -> {
            List<OotdImageClothes> ootdImageClothesList = ootdImage.getClothesTags().stream().map(clothesTag -> {
                Clothes clothes = clothesRepository.findById(clothesTag.getClothesId()).orElseThrow();
                Coordinate coordinate = new Coordinate(clothesTag.getXRate(), clothesTag.getYRate());
                DeviceSize deviceSize = new DeviceSize(clothesTag.getDeviceWidth(), clothesTag.getDeviceHeight());

                return OotdImageClothes.createOotdImageClothesBy(clothes, coordinate, deviceSize);
            }).toList();

            return OotdImage.createOotdImageBy(Images.of(ootdImage.getOotdImage()), ootdImageClothesList);
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

    @Transactional
    public void updateContentsAndIsPrivate(Long id, OotdPatchReq request) {

        Ootd ootd = ootdRepository.findById(id).orElseThrow();

        userService.checkValidUser(ootd.getWriter());

        ootd.updateIsPrivate(request.getIsPrivate());
    }

    @Transactional
    public void updateAll(Long id, OotdPutReq request) {

        Ootd ootd = ootdRepository.findById(id).orElseThrow();

        userService.checkValidUser(ootd.getWriter());

        Set<Images> imagesSet = new HashSet<>();

        List<OotdImage> ootdImages = request.getOotdImages().stream().map(ootdImage -> {
            List<OotdImageClothes> ootdImageClothesList = ootdImage.getClothesTags().stream().map(clothesTag -> {
                Clothes clothes = clothesRepository.findById(clothesTag.getClothesId()).orElseThrow();
                Coordinate coordinate = new Coordinate(clothesTag.getXRate(), clothesTag.getYRate());
                DeviceSize deviceSize = new DeviceSize(clothesTag.getDeviceWidth(), clothesTag.getDeviceHeight());

                return OotdImageClothes.createOotdImageClothesBy(clothes, coordinate, deviceSize);
            }).toList();

            Images images = Images.of(ootdImage.getOotdImage());
            imagesSet.add(images);
            return OotdImage.createOotdImageBy(images, ootdImageClothesList);
        }).toList();

        List<Style> styles = styleRepository.findAllById(request.getStyles());
        List<OotdStyle> ootdStyles = OotdStyle.createOotdStylesBy(styles);

        // 변경이 되지 않은 이미지 삭제
        ootd.getOotdImages().stream().map(OotdImage::getImages).forEach(i -> {
            if (!imagesSet.contains(i)) {
                imagesService.deleteImagesByUrlToS3(i);
            }
        });

        ootd.updateAll(request.getContent(),
                request.getIsPrivate(),
                ootdImages,
                ootdStyles);
    }

    @Transactional
    public void deleteOotd(Long id) {
        Ootd ootd = ootdRepository.findById(id).orElseThrow();
        userService.checkValidUser(ootd.getWriter());
        ootd.deleteOotd();
    }

    /**
     * 기본적인 단건조회 API 입니다.
     * 비공개글은 본인글이 아니면 볼 수 없습니다.
     */
    @Transactional
    public OotdGetRes getOotd(Long ootdId, User loginUser) {

        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();

        checkOotd(ootd, loginUser);

        return new OotdGetRes(ootd, loginUser);
    }

    @RLockCustom(type = RLockType.OOTD_VIEW_COUNT, key = "#ootdId")
    @Transactional
    @Async
    public void increaseViewCount(Long ootdId, User loginUser) {

        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();

        if (isDuplicatedView(ootdId, loginUser.getId())) {
            return;
        }

        ootd.increaseViewCount();
    }

    /**
     * Redis 를 이용해 해당 유저가 중복 조회인지 확인합니다.
     * TTL 을 두고 해당 시간이 지나면 다시 조회수 카운트가 됩니다.
     */
    private Boolean isDuplicatedView(Long ootdId, Long userId) {
        String ootdKey = RedisKey.OOTD.makeKeyWith(ootdId);
        String value = String.valueOf(userId);

        // 중복 조회인 경우
        if (redisDao.getValuesSet(ootdKey).contains(value)) {
            return true;
        }

        // 키가 존재하지 않을시 만료기간 지정
        if (redisDao.getValuesSet(ootdKey).isEmpty()) {
            redisDao.setValuesSet(ootdKey, value);
            redisDao.setExpiration(ootdKey, Duration.ofSeconds(60 * 60 * 24));
        } else {
            redisDao.setValuesSet(ootdKey, value);
        }

        return false;
    }

    private void checkOotd(Ootd ootd, User user) {
        if (ootd.isPrivate() && !ootd.getWriter().equals(user)) {
            throw new CustomException(ErrorCode.PRIVATE);
        }

        if (userBlockRepository.existUserBlock(ootd.getWriter().getId(), user.getId())) {
            throw new CustomException(ErrorCode.BLOCK_USER_CONTENTS);
        }
    }

    /**
     * 전체 ootd 조회
     * 삭제된글, 차단된글, 신고수가 특정 수 이상, 비공개글은 가져오지 않습니다.
     * 단, 비공개글이어도 본인 작성글이면 가져옵니다.
     */
    @Transactional
    public SliceImpl<OotdGetAllRes> getOotds(User loginUser, int page) {

        int size = 20;
        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        Slice<Ootd> ootds = ootdRepository.findAllByUserIdAndWriterIdNotIn(loginUser.getId(), nonAccessibleUserIds,
                pageable);

        List<OotdGetAllRes> ootdGetAllResList = ootds.stream()
                .map(ootd -> new OotdGetAllRes(ootd, loginUser))
                .collect(Collectors.toList());

        return new SliceImpl<>(ootdGetAllResList, pageable, ootds.hasNext());
    }

    /**
     * 로그인된 사용자 기준으로 좋아요를 추가합니다.
     */
    @RLockCustom(type = RLockType.OOTD_LIKE_COUNT, key = "#ootdId")
    @Transactional
    public void addLike(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();

        ootd.addLike(loginUser);
        ootd.increaseLike();
        notifyOotdLike(ootd.getWriter(), loginUser, ootd.getFirstImage(), ootd.getId());
    }

    private void notifyOotdLike(User receiver, User sender, String imageUrl, Long id) {

        if (receiver.equals(sender)) { // OOTD 작성자와 댓글 작성자가 같으면 알람 X
            return;
        }

        eventPublisher.publishEvent(NotificationEvent.builder()
                .receiver(receiver)
                .sender(sender)
                .notificationType(NotificationType.LIKE)
                .goUrl("ootd/" + id)
                .imageUrl(imageUrl)
                .build());
    }

    @RLockCustom(type = RLockType.OOTD_LIKE_COUNT, key = "#ootdId")
    @Transactional
    public void cancelLike(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        ootd.cancelLike(loginUser);
        ootd.decreaseLike();
    }

    /**
     * 로그인된 사용자 기준으로 북마크를 추가합니다.
     */
    @RLockCustom(type = RLockType.OOTD_BOOKMARK_COUNT, key = "#ootdId")
    @Transactional
    public void addBookmark(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        ootd.addBookmark(loginUser);
        ootd.increaseBookmarkCount();
    }

    @RLockCustom(type = RLockType.OOTD_BOOKMARK_COUNT, key = "#ootdId")
    @Transactional
    public void cancelBookmark(Long ootdId, User loginUser) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow();
        ootd.cancelBookmark(loginUser);
        ootd.decreaseBookmarkCount();
    }

    /**
     * OOTD 상세 조회시, 현재 OOTD 작성자의 다른 OOTD 정보를 제공한다.
     * 본인 조회시에도 비공개글은 조회되지 ㅏㅇㄴ흣ㅂ니다.
     */
    @Transactional
    public CommonSliceResponse<OotdGetOtherRes> getOotdOther(OotdGetOtherReq request, User loginUser) {

        Long writerId = request.getUserId();
        Long ootdId = request.getOotdId();
        Pageable pageable = request.toPageable();

        if (!request.getUserId().equals(loginUser.getId())
                && userBlockRepository.existUserBlock(request.getUserId(), loginUser.getId())) {
            throw new CustomException(ErrorCode.BLOCK_USER_CONTENTS);
        }

        Slice<Ootd> ootds = ootdRepository.findAllByUserIdAndOotdId(writerId, ootdId, pageable);

        List<OotdGetOtherRes> ootdGetOtherResList = ootds.stream()
                .map(OotdGetOtherRes::new)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(ootdGetOtherResList, pageable, ootds.isLast());
    }

    /**
     * OOTD 상세 조회시, 현재 OOTD 와 동일한 스타일의 다른 OOTD 를 제공합니다.
     * 본인 조회시에도 비공개글은 조회되지 않습니다.
     */
    @Transactional
    public CommonSliceResponse<OotdGetSimilarRes> getOotdSimilar(OotdGetSimilarReq request, User loginUser) {

        Long ootdId = request.getOotdId();
        Pageable pageable = request.toPageable();

        Ootd nowOotd = ootdRepository.findById(ootdId).orElseThrow();
        List<Style> styles = nowOotd.getStyles().stream()
                .map(OotdStyle::getStyle)
                .collect(Collectors.toList());

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        Slice<Ootd> ootds = ootdRepository.findAllByOotdIdNotAndStylesWriterIdNotIn(ootdId,
                styles,
                nonAccessibleUserIds,
                pageable);

        List<OotdGetSimilarRes> ootdGetSimilarResList = ootds.stream()
                .map(OotdGetSimilarRes::new)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(ootdGetSimilarResList, pageable, ootds.isLast());
    }

    /**
     * 마이페이지에서 OOTD 조회시 해당 유저가 가진 OOTD 정보를 제공합니다.
     * 본인 조회시, 비공개글 조회가 가능합니다. 그래서 loginUser 정보를 받아 검증할 필요가 있습니다.
     */
    @Transactional
    public CommonSliceResponse<OotdGetByUserRes> getOotdByUser(User loginUser, OotdGetByUserReq request) {

        Long userId = request.getUserId();
        Long loginUserId = loginUser.getId();
        Pageable pageable = request.toPageable();

        if (userBlockRepository.existUserBlock(userId, loginUserId)) {
            throw new CustomException(ErrorCode.BLOCK_USER_CONTENTS);
        }

        Slice<Ootd> ootds = ootdRepository.findAllByUserIdAndLoginUserId(userId, loginUserId, pageable);

        List<OotdGetByUserRes> ootdGetByUserResList = ootds.stream()
                .map(OotdGetByUserRes::new)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(ootdGetByUserResList, pageable, ootds.isLast());
    }

    /**
     * 특정 유저의 옷을 사용한 OOTD 를 조회합니다.
     * 본인 조회시에도 비공개글 조회가 가능합니다.
     */
    @Transactional
    public CommonPageResponse<OotdGetClothesRes> getOotdByClothes(User loginUser, OotdGetClothesReq request) {

        Pageable pageable = request.toPageable();

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        Page<OotdImage> ootdImages = ootdImageRepository.findByClothesAndUserIdAndLoginUserIdAndWriterIdNotIn(
                loginUser.getId(),
                request.getClothesId(),
                nonAccessibleUserIds,
                pageable);

        List<OotdGetClothesRes> ootdGetClothesResList = ootdImages.stream()
                .map(OotdGetClothesRes::new)
                .collect(Collectors.toList());

        return new CommonPageResponse<>(ootdGetClothesResList,
                pageable,
                ootdImages.isLast(),
                ootdImages.getTotalElements());
    }

    /**
     * 검색 기능에서 사용하는 ootd 검색 메소드입니다.
     */
    @Transactional
    public CommonPageResponse<OotdSearchRes> searchOotds(OotdSearchSvcReq request, User loginUser) {

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        CommonPageResponse<Ootd> findOotds = ootdRepository.searchOotds(request.getSearchText(),
                request.getBrandIds(),
                request.getCategoryIds(),
                request.getColorIds(),
                request.getWriterGender(),
                nonAccessibleUserIds,
                request.getSortCriteria(),
                request.getPageable());

        List<OotdSearchRes> ootdSearchRes = findOotds.getContent()
                .stream()
                .map(OotdSearchRes::of)
                .toList();

        return new CommonPageResponse<OotdSearchRes>(ootdSearchRes, request.getPageable(), findOotds.getIsLast(),
                findOotds.getTotal());
    }

    /**
     * 오늘 입기 좋은 옷 API를 구현합니다. OOTD는 최대 10개까지 반환합니다.
     */
    public OotdTodayRes getOotdToday(OotdTodayReq request, User loginUser) {
        Temperatures temperatures = weatherService.getTemperatures(request.getLat(), request.getLng(), LocalDate.now());
        List<Ootd> ootdToday = ootdRepository.findOotdToday(temperatures.getHighestTemperature(),
                temperatures.getLowestTemperature(), loginUser);

        List<OotdTodayRes.Ootd> ootdResList = new ArrayList<>();
        for (Ootd ootd : ootdToday) {
            List<Clothes> taggedClothesList = ootd.getOotdImages()
                    .stream()
                    .flatMap(images -> images.getOotdImageClothesList().stream()
                            .map(OotdImageClothes::getClothes))
                    .toList();
            Map<Clothes, Set<Clothes>> userClothes = ootdRepository.findMatchingUserClothes(taggedClothesList,
                    loginUser);

            Clothes taggedClothes = findTaggedClothesWithSimilarMyClothesNotEmpty(taggedClothesList, userClothes);
            if (taggedClothes != null) {
                OotdTodayRes.Ootd.Clothes taggedClothesRes = OotdTodayRes.Ootd.Clothes.from(taggedClothes);
                Clothes similarMyClothes = userClothes.get(taggedClothes).stream().iterator().next();
                OotdTodayRes.Ootd.Clothes similarMyClothesRes = OotdTodayRes.Ootd.Clothes.from(similarMyClothes);
                OotdTodayRes.Ootd ootdRes = OotdTodayRes.Ootd.withId(ootd.getId())
                        .ootdImageUrl(ootd.getFirstImage())
                        .taggedClothes(taggedClothesRes)
                        .similarMyClothes(similarMyClothesRes)
                        .build();
                ootdResList.add(ootdRes);
            }
        }
        return OotdTodayRes.builder()
                .ootdList(ootdResList)
                .highestTemp(temperatures.getHighestTemperature())
                .lowestTemp(temperatures.getLowestTemperature())
                .build();
    }

    private Clothes findTaggedClothesWithSimilarMyClothesNotEmpty(List<Clothes> taggedClothesList,
            Map<Clothes, Set<Clothes>> userClothes) {
        for (Clothes clothes : taggedClothesList) {
            if (!userClothes.get(clothes).isEmpty()) {
                return clothes;
            }
        }
        return null;
    }
}
