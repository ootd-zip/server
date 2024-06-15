package zip.ootd.ootdzip.ootd.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.constant.RedisKey;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.oauth.OAuthUtils;
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
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class OotdServiceTest extends IntegrationTestSupport {

    @Autowired
    private OotdService ootdService;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private RedisDao redisDao;

    private final String OOTD_IMAGE_URL = "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png";

    private final String OOTD_IMAGE_URL1 = "https://ootdzip/0459a64c-89d9-4c63-be21_2024-03-27.png";

    @AfterEach
    void tearDown() {
        redisDao.deleteAll();
    }

    @DisplayName("OOTD 게시글 저장")
    @Test
    void save() {
        // given
        User user = createUserBy("유저");
        Clothes clothes = createClothesBy(user, true, "1");
        Clothes clothes1 = createClothesBy(user, true, "2");

        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(clothes.getId());
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(clothes1.getId());
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPostReq.OotdImageReq ootdImageReq = new OotdPostReq.OotdImageReq();
        ootdImageReq.setOotdImage(OOTD_IMAGE_URL);
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        Style style = Style.builder().name("올드머니").build();
        Style savedStyle = styleRepository.save(style);
        Style style1 = Style.builder().name("블루코어").build();
        Style savedStyle1 = styleRepository.save(style1);

        OotdPostReq ootdPostReq = new OotdPostReq();
        ootdPostReq.setIsPrivate(false);
        ootdPostReq.setContent("테스트");
        ootdPostReq.setStyles(Arrays.asList(savedStyle.getId(), savedStyle1.getId()));
        ootdPostReq.setOotdImages(List.of(ootdImageReq));

        // when
        Ootd result = ootdService.postOotd(ootdPostReq, user);

        // then
        Ootd savedResult = ootdRepository.findById(result.getId()).get();

        assertThat(savedResult).extracting("id", "isPrivate", "contents")
                .contains(result.getId(), false, "테스트");

        assertThat(savedResult.getStyles())
                .hasSize(2)
                .extracting("style.name")
                .containsExactlyInAnyOrder("올드머니", "블루코어");

        assertThat(savedResult.getOotdImages())
                .hasSize(1);

        assertThat(savedResult.getOotdImages().get(0).getOotdImageClothesList())
                .hasSize(2)
                .extracting("clothes.id", "coordinate.xRate", "coordinate.yRate", "deviceSize.deviceWidth",
                        "deviceSize.deviceHeight")
                .containsExactlyInAnyOrder(
                        tuple(clothes.getId(), "22.33", "33.44", 100L, 50L),
                        tuple(clothes1.getId(), "33.44", "44.55", 100L, 50L)
                );
    }

    @DisplayName("OOTD 공개여부를 변경한다.")
    @Test
    void updateContentsAndIsPrivate() {
        // given
        User user = createUserBy("유저1");
        makeAuthenticatedUserBy(user);
        Ootd ootd = createOotdBy(user, "안녕", false);

        OotdPatchReq ootdPatchReq = new OotdPatchReq();
        ootdPatchReq.setIsPrivate(true);

        // when
        ootdService.updateContentsAndIsPrivate(ootd.getId(), ootdPatchReq);

        // then
        assertThat(ootd).extracting("id", "isPrivate")
                .contains(ootd.getId(), true);
    }

    @DisplayName("OOTD 전체 업데이트를 한다")
    @Test
    void updateAll() {
        // given
        User user = createUserBy("유저1");
        makeAuthenticatedUserBy(user);
        Ootd ootd = createOotdBy(user, "안녕", false);
        Clothes clothes = createClothesBy(user, true, "3");
        Clothes clothes1 = createClothesBy(user, true, "4");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(clothes.getId());
        clothesTagReq.setDeviceWidth(20L);
        clothesTagReq.setDeviceHeight(30L);
        clothesTagReq.setXRate("11.22");
        clothesTagReq.setYRate("22.33");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(clothes1.getId());
        clothesTagReq1.setDeviceWidth(20L);
        clothesTagReq1.setDeviceHeight(30L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("55.66");

        OotdPutReq.OotdImageReq ootdImageReq = new OotdPutReq.OotdImageReq();
        ootdImageReq.setOotdImage(OOTD_IMAGE_URL1);
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        Style style = Style.builder().name("아메카제").build();
        Style savedStyle = styleRepository.save(style);
        Style style1 = Style.builder().name("미니멀").build();
        Style savedStyle1 = styleRepository.save(style1);

        OotdPutReq ootdPutReq = new OotdPutReq();
        ootdPutReq.setContent("잘가");
        ootdPutReq.setIsPrivate(true);
        ootdPutReq.setOotdImages(List.of(ootdImageReq));
        ootdPutReq.setStyles(Arrays.asList(savedStyle.getId(), savedStyle1.getId()));

        // when
        ootdService.updateAll(ootd.getId(), ootdPutReq);

        // then
        Ootd savedResult = ootdRepository.findById(ootd.getId()).get();

        assertThat(savedResult).extracting("id", "isPrivate", "contents")
                .contains(ootd.getId(), true, "잘가");

        assertThat(savedResult.getStyles())
                .hasSize(2)
                .extracting("style.name")
                .containsExactlyInAnyOrder("아메카제", "미니멀");

        assertThat(savedResult.getOotdImages())
                .hasSize(1);

        assertThat(savedResult.getOotdImages().get(0).getOotdImageClothesList())
                .hasSize(2)
                .extracting("clothes.id", "coordinate.xRate", "coordinate.yRate", "deviceSize.deviceWidth",
                        "deviceSize.deviceHeight")
                .containsExactlyInAnyOrder(
                        tuple(clothes.getId(), "11.22", "22.33", 20L, 30L),
                        tuple(clothes1.getId(), "33.44", "55.66", 20L, 30L)
                );
    }

    @DisplayName("OOTD 를 삭제한다.")
    @Test
    void delete() {
        // given
        User user = createUserBy("유저1");
        makeAuthenticatedUserBy(user);
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        ootdService.deleteOotd(ootd.getId());
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getIsDeleted()).isEqualTo(true);
        assertThat(result.getDeletedAt()).isNotNull();
    }

    @DisplayName("좋아요를 한다.")
    @Test
    void addLike() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        ootdService.addLike(ootd.getId(), user);
        ootdService.addLike(ootd.getId(), user1);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdLikes())
                .hasSize(2)
                .extracting("user.id")
                .contains(user.getId(), user1.getId());
    }

    @DisplayName("좋아요를 취소한다")
    @Test
    void cancelLike() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        ootdService.addLike(ootd.getId(), user);
        ootdService.addLike(ootd.getId(), user1);

        // when
        ootdService.cancelLike(ootd.getId(), user);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdLikes())
                .hasSize(1)
                .extracting("user.id")
                .contains(user1.getId());
    }

    @DisplayName("OOTD 북마크를 추가한다.")
    @Test
    void addBookmark() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        ootdService.addBookmark(ootd.getId(), user);
        ootdService.addBookmark(ootd.getId(), user1);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdBookmarks())
                .hasSize(2)
                .extracting("user.id")
                .contains(user.getId(), user1.getId());
    }

    @DisplayName("OOTD 북마크를 취소한다.")
    @Test
    void cancelBookmark() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        ootdService.addBookmark(ootd.getId(), user);
        ootdService.addBookmark(ootd.getId(), user1);

        // when
        ootdService.cancelBookmark(ootd.getId(), user);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdBookmarks())
                .hasSize(1)
                .extracting("user.id")
                .contains(user1.getId());
    }

    @DisplayName("ootd 를 조회한다.")
    @ParameterizedTest
    @CsvSource({
            "true",
            "false"
    })
    void getOotd(boolean isPrivate) {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", isPrivate);

        // when
        OotdGetRes result = ootdService.getOotd(ootd.getId(), user);

        // then
        assertThat(result.getId()).isEqualTo(ootd.getId());
        assertThat(redisDao.getValuesSet(RedisKey.OOTD.makeKeyWith(ootd.getId())))
                .contains(user.getId() + ""); // redis 에 저장된 유저확인
    }

    @DisplayName("ootd 를 여러번 조회한다.")
    @Test
    void getOotdSeveral() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when & then
        ootdService.getOotd(ootd.getId(), user);
        assertThat(redisDao.getValuesSet(RedisKey.OOTD.makeKeyWith(ootd.getId())))
                .contains(user.getId() + "");

        ootdService.getOotd(ootd.getId(), user1);
        assertThat(redisDao.getValuesSet(RedisKey.OOTD.makeKeyWith(ootd.getId())))
                .contains(user.getId() + "", user1.getId() + "");

        ootdService.getOotd(ootd.getId(), user);
        assertThat(ootd.getViewCount()).isEqualTo(2);
    }

    @DisplayName("ootd 를 조회시 존재하는 ootd 이어야 한다.")
    @Test
    void getOotdWithoutOotd() {
        // given
        User user = createUserBy("유저");

        // when & then
        assertThatThrownBy(() -> ootdService.getOotd(123456789L, user)).isInstanceOf(
                NoSuchElementException.class);

    }

    @DisplayName("ootd 를 조회시 다른 사람 비공개글 조회시 조회가 안된다.")
    @Test
    void getPrivateOotd() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", true);

        // when & then
        assertThatThrownBy(() -> ootdService.getOotd(ootd.getId(), user1)).isInstanceOf(
                CustomException.class);
    }

    @DisplayName("ootd 작성자의 다른 ootd 를 조회한다.")
    @Test
    void getOotdOther() {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Ootd ootd1 = createOotdBy(user, "안녕", false);
        Ootd ootd2 = createOotdBy(user, "안녕", false);
        Ootd ootd3 = createOotdBy(user, "안녕", false);

        OotdGetOtherReq ootdGetOtherReq = new OotdGetOtherReq();
        ootdGetOtherReq.setOotdId(ootd.getId());
        ootdGetOtherReq.setUserId(user.getId());
        ootdGetOtherReq.setPage(0);
        ootdGetOtherReq.setSize(10);
        ootdGetOtherReq.setSortCriteria("createdAt");
        ootdGetOtherReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<OotdGetOtherRes> result = ootdService.getOotdOther(ootdGetOtherReq, user);

        // then
        // ootd 는 작성시간 내림차순으로 정렬된다.
        assertThat(result.getContent())
                .hasSize(3)
                .extracting("id")
                .containsExactly(ootd3.getId(), ootd2.getId(), ootd1.getId());
    }

    @DisplayName("ootd 작성자의 다른 ootd 를 조회시 비공개글은 조회되지 않는다.")
    @Test
    void getOotdOtherWithoutPrivate() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Ootd ootd1 = createOotdBy(user, "안녕", false);
        Ootd ootd2 = createOotdBy(user, "안녕", true);
        Ootd ootd3 = createOotdBy(user, "안녕", true);

        OotdGetOtherReq ootdGetOtherReq = new OotdGetOtherReq();
        ootdGetOtherReq.setOotdId(ootd.getId());
        ootdGetOtherReq.setUserId(user.getId());
        ootdGetOtherReq.setPage(0);
        ootdGetOtherReq.setSize(10);
        ootdGetOtherReq.setSortCriteria("createdAt");
        ootdGetOtherReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<OotdGetOtherRes> result = ootdService.getOotdOther(ootdGetOtherReq, user);

        // then
        // ootd 는 작성시간 내림차순으로 정렬된다.
        assertThat(result.getContent())
                .hasSize(1)
                .extracting("id")
                .containsExactly(ootd1.getId());
    }

    @DisplayName("ootd 작성자와 비슷한 ootd 를 조회한다.")
    @Test
    void getOotdSimilar() {
        // given
        Style style1 = createStyleBy("올드머니");
        Style style2 = createStyleBy("블루코어");
        Style style3 = createStyleBy("고프코어");

        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(style1, style2));
        Ootd ootd1 = createOotdBy(user, "안녕1", false, Arrays.asList(style1, style2));
        Ootd ootd2 = createOotdBy(user, "안녕2", false, Arrays.asList(style1, style2));
        Ootd ootd3 = createOotdBy(user1, "안녕3", false, Arrays.asList(style1, style2));

        // 한 개라도 동일한 스타일이 있으면 포함
        Ootd ootd4 = createOotdBy(user1, "안녕4", false, List.of(style1));
        Ootd ootd5 = createOotdBy(user1, "안녕5", false, List.of(style2));
        Ootd ootd6 = createOotdBy(user1, "안녕6", false, Arrays.asList(style1, style3));

        // 포함되는 스타일이 하나도 없을시 포함하지 않음
        Ootd ootd7 = createOotdBy(user1, "안녕7", false, List.of(style3));

        OotdGetSimilarReq ootdGetSimilarReq = new OotdGetSimilarReq();
        ootdGetSimilarReq.setOotdId(ootd.getId());
        ootdGetSimilarReq.setPage(0);
        ootdGetSimilarReq.setSize(10);
        ootdGetSimilarReq.setSortCriteria("createdAt");
        ootdGetSimilarReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<OotdGetSimilarRes> result = ootdService.getOotdSimilar(ootdGetSimilarReq, user);

        // then
        // ootd 는 작성시간 내림차순으로 정렬된다.
        assertThat(result.getContent())
                .hasSize(6)
                .extracting("id")
                .containsExactly(ootd6.getId(), ootd5.getId(), ootd4.getId(),
                        ootd3.getId(), ootd2.getId(), ootd1.getId());
    }

    @DisplayName("ootd 작성자와 비슷한 ootd 를 조회시 비공개글은 조회가 되지 않는다.(본인이어도)")
    @Test
    void getOotdSimilarWithoutPrivate() {
        // given
        Style style1 = createStyleBy("올드머니");
        Style style2 = createStyleBy("블루코어");
        Style style3 = createStyleBy("고프코어");

        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(style1, style2));
        Ootd ootd1 = createOotdBy(user, "안녕1", false, Arrays.asList(style1, style2));
        Ootd ootd2 = createOotdBy(user, "안녕2", true, Arrays.asList(style1, style2));
        Ootd ootd3 = createOotdBy(user1, "안녕3", true, Arrays.asList(style1, style2));

        // 한 개라도 동일한 스타일이 있으면 포함
        Ootd ootd4 = createOotdBy(user1, "안녕4", false, List.of(style1));
        Ootd ootd5 = createOotdBy(user1, "안녕5", true, List.of(style2));
        Ootd ootd6 = createOotdBy(user1, "안녕6", true, Arrays.asList(style1, style3));

        // 포함되는 스타일이 하나도 없을시 포함하지 않음
        Ootd ootd7 = createOotdBy(user1, "안녕7", false, List.of(style3));

        OotdGetSimilarReq ootdGetSimilarReq = new OotdGetSimilarReq();
        ootdGetSimilarReq.setOotdId(ootd.getId());
        ootdGetSimilarReq.setPage(0);
        ootdGetSimilarReq.setSize(10);
        ootdGetSimilarReq.setSortCriteria("createdAt");
        ootdGetSimilarReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<OotdGetSimilarRes> result = ootdService.getOotdSimilar(ootdGetSimilarReq, user);

        // then
        // ootd 는 작성시간 내림차순으로 정렬된다.
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("id")
                .containsExactly(ootd4.getId(), ootd1.getId());
    }

    @DisplayName("특정 유저의 ootd 를 전체 조회한다. 본인글은 비공개여도 조회가 가능하다.")
    @Test
    void getOotdByUser() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Ootd ootd1 = createOotdBy(user, "안녕", false);
        Ootd ootd2 = createOotdBy(user, "안녕", true);
        Ootd ootd3 = createOotdBy(user, "안녕", true);
        Ootd ootd4 = createOotdBy(user1, "안녕", false);
        Ootd ootd5 = createOotdBy(user1, "안녕", true);

        OotdGetByUserReq ootdGetByUserReq = new OotdGetByUserReq();
        ootdGetByUserReq.setUserId(user.getId());
        ootdGetByUserReq.setPage(0);
        ootdGetByUserReq.setSize(10);
        ootdGetByUserReq.setSortCriteria("createdAt");
        ootdGetByUserReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<OotdGetByUserRes> result = ootdService.getOotdByUser(user, ootdGetByUserReq);

        // then
        // ootd 는 작성시간 내림차순으로 정렬된다.
        assertThat(result.getContent())
                .hasSize(4)
                .extracting("id")
                .containsExactly(ootd3.getId(), ootd2.getId(), ootd1.getId(), ootd.getId());
    }

    @DisplayName("특정 유저의 ootd 를 전체 조회한다. 본인이 아니면 비공개글은 조회가 불가능하다.")
    @Test
    void getOotdByUserWithoutIsPrivate() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Ootd ootd1 = createOotdBy(user, "안녕", false);
        Ootd ootd2 = createOotdBy(user, "안녕", true);
        Ootd ootd3 = createOotdBy(user, "안녕", true);
        Ootd ootd4 = createOotdBy(user1, "안녕", false);
        Ootd ootd5 = createOotdBy(user1, "안녕", true);

        OotdGetByUserReq ootdGetByUserReq = new OotdGetByUserReq();
        ootdGetByUserReq.setUserId(user.getId());
        ootdGetByUserReq.setPage(0);
        ootdGetByUserReq.setSize(10);
        ootdGetByUserReq.setSortCriteria("createdAt");
        ootdGetByUserReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<OotdGetByUserRes> result = ootdService.getOotdByUser(user1, ootdGetByUserReq);

        // then
        // ootd 는 작성시간 내림차순으로 정렬된다.
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("id")
                .containsExactly(ootd1.getId(), ootd.getId());
    }

    @DisplayName("옷을 사용한 OOTD를 가져온다.")
    @Test
    void findByClothes() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        Clothes clothes = createClothesBy(user, true, "0");
        Clothes clothes1 = createClothesBy(user, true, "1");
        Clothes clothes2 = createClothesBy(user, true, "2");

        Ootd ootd = createOotdBy2(user, "안녕", false, Arrays.asList(clothes, clothes1, clothes2));

        // 다른 유저의 옷은 포함되지 않음
        Ootd ootd1 = createOotdBy2(user1, "안녕1", false, Arrays.asList(clothes, clothes1, clothes2));

        // 한 개라도 동일한 옷이 있으면 포함
        Ootd ootd2 = createOotdBy2(user, "안녕2", false, List.of(clothes));
        Ootd ootd3 = createOotdBy2(user, "안녕3", false, Arrays.asList(clothes, clothes1));
        Ootd ootd4 = createOotdBy2(user, "안녕4", false, Arrays.asList(clothes, clothes2));

        // 포함되는 옷이 하나도 없을시 포함하지 않음
        Ootd ootd5 = createOotdBy2(user, "안녕5", false, List.of(clothes1));
        Ootd ootd6 = createOotdBy2(user, "안녕6", false, Arrays.asList(clothes1, clothes2));

        // 비공개글이어도 본인이면 포함
        Ootd ootd7 = createOotdBy2(user, "안녕7", true, Arrays.asList(clothes, clothes1, clothes2));

        // 차단, 신고수, 삭제된건 포함안함
        Ootd ootd8 = createOotdBy2(user, "안녕8", false, Arrays.asList(clothes, clothes1, clothes2));
        ootd8.setIsBlocked(true);
        Ootd ootd9 = createOotdBy2(user, "안녕9", false, Arrays.asList(clothes, clothes1, clothes2));
        ootd9.setReportCount(10);
        Ootd ootd10 = createOotdBy2(user, "안녕10", false, Arrays.asList(clothes, clothes1, clothes2));
        ootd10.setIsDeleted(true);

        OotdGetClothesReq ootdGetClothesReq = new OotdGetClothesReq();
        ootdGetClothesReq.setClothesId(clothes.getId());
        ootdGetClothesReq.setSize(10);
        ootdGetClothesReq.setPage(0);
        ootdGetClothesReq.setSortCriteria("createdAt");
        ootdGetClothesReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonPageResponse<OotdGetClothesRes> results = ootdService.getOotdByClothes(user, ootdGetClothesReq);

        // then
        assertThat(results.getTotal()).isEqualTo(5);
        assertThat(results.getContent()).hasSize(5)
                .extracting("id")
                .containsExactly(ootd7.getId(), ootd4.getId(), ootd3.getId(), ootd2.getId(), ootd.getId());
    }

    @DisplayName("옷을 사용한 OOTD 가져올시 본인이 아니면 비공개글은 가져오지 않는다.")
    @Test
    void findByClothesWithIsPrivate() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        Clothes clothes = createClothesBy(user1, true, "0");
        Clothes clothes1 = createClothesBy(user1, true, "1");
        Clothes clothes2 = createClothesBy(user1, true, "2");

        // 다른 유저의 옷은 포함되지 않음
        Ootd ootd = createOotdBy2(user, "안녕", false, Arrays.asList(clothes, clothes1, clothes2));

        Ootd ootd1 = createOotdBy2(user1, "안녕1", false, Arrays.asList(clothes, clothes1, clothes2));

        // 다른 유저가 조회시 비공개글은 안보임
        Ootd ootd2 = createOotdBy2(user1, "안녕2", true, Arrays.asList(clothes, clothes1, clothes2));

        OotdGetClothesReq ootdGetClothesReq = new OotdGetClothesReq();
        ootdGetClothesReq.setClothesId(clothes.getId());
        ootdGetClothesReq.setSize(10);
        ootdGetClothesReq.setPage(0);
        ootdGetClothesReq.setSortCriteria("createdAt");
        ootdGetClothesReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonPageResponse<OotdGetClothesRes> results = ootdService.getOotdByClothes(user, ootdGetClothesReq);

        // then
        assertThat(results.getTotal()).isEqualTo(1);
        assertThat(results.getContent()).hasSize(1)
                .extracting("id")
                .containsExactly(ootd1.getId());
    }

    private Ootd createOotdBy2(User user, String content, boolean isPrivate, List<Clothes> clothesList) {

        Coordinate coordinate = new Coordinate("22.33", "33.44");
        Coordinate coordinate1 = new Coordinate("33.44", "44.55");

        DeviceSize deviceSize = new DeviceSize(100L, 50L);
        DeviceSize deviceSize1 = new DeviceSize(100L, 50L);

        List<OotdImageClothes> ootdImageClothesList = new ArrayList<>();
        for (Clothes clothes : clothesList) {
            OotdImageClothes ootdImageClothes = OotdImageClothes.builder().clothes(clothes)
                    .coordinate(coordinate)
                    .deviceSize(deviceSize)
                    .build();

            ootdImageClothesList.add(ootdImageClothes);
        }

        OotdImage ootdImage = OotdImage.createOotdImageBy(OOTD_IMAGE_URL1, ootdImageClothesList);

        Style style = Style.builder().name("올드머니").build();
        styleRepository.save(style);
        Style style1 = Style.builder().name("블루코어").build();
        styleRepository.save(style1);

        OotdStyle ootdStyle = OotdStyle.createOotdStyleBy(style);
        OotdStyle ootdStyle1 = OotdStyle.createOotdStyleBy(style1);

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                List.of(ootdImage),
                Arrays.asList(ootdStyle, ootdStyle1));

        return ootdRepository.save(ootd);
    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate, List<Style> styles) {

        Clothes clothes = createClothesBy(user, true, "1");
        Clothes clothes1 = createClothesBy(user, true, "2");

        Coordinate coordinate = new Coordinate("22.33", "33.44");
        Coordinate coordinate1 = new Coordinate("33.44", "44.55");

        DeviceSize deviceSize = new DeviceSize(100L, 50L);
        DeviceSize deviceSize1 = new DeviceSize(100L, 50L);

        OotdImageClothes ootdImageClothes = OotdImageClothes.builder().clothes(clothes)
                .coordinate(coordinate)
                .deviceSize(deviceSize)
                .build();

        OotdImageClothes ootdImageClothes1 = OotdImageClothes.builder().clothes(clothes1)
                .coordinate(coordinate1)
                .deviceSize(deviceSize1)
                .build();

        OotdImage ootdImage = OotdImage.createOotdImageBy(OOTD_IMAGE_URL,
                Arrays.asList(ootdImageClothes, ootdImageClothes1));

        List<OotdStyle> ootdStyles = OotdStyle.createOotdStylesBy(styles);

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                List.of(ootdImage),
                ootdStyles);

        return ootdRepository.save(ootd);
    }

    private Style createStyleBy(String name) {

        Style style = Style.builder().name(name).build();
        return styleRepository.save(style);
    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate) {

        Clothes clothes = createClothesBy(user, true, "1");
        Clothes clothes1 = createClothesBy(user, true, "2");

        Coordinate coordinate = new Coordinate("22.33", "33.44");
        Coordinate coordinate1 = new Coordinate("33.44", "44.55");

        DeviceSize deviceSize = new DeviceSize(100L, 50L);
        DeviceSize deviceSize1 = new DeviceSize(100L, 50L);

        OotdImageClothes ootdImageClothes = OotdImageClothes.builder().clothes(clothes)
                .coordinate(coordinate)
                .deviceSize(deviceSize)
                .build();

        OotdImageClothes ootdImageClothes1 = OotdImageClothes.builder().clothes(clothes1)
                .coordinate(coordinate1)
                .deviceSize(deviceSize1)
                .build();

        OotdImage ootdImage = OotdImage.createOotdImageBy(OOTD_IMAGE_URL,
                Arrays.asList(ootdImageClothes, ootdImageClothes1));

        Style style = Style.builder().name("올드머니").build();
        styleRepository.save(style);
        Style style1 = Style.builder().name("블루코어").build();
        styleRepository.save(style1);

        OotdStyle ootdStyle = OotdStyle.createOotdStyleBy(style);
        OotdStyle ootdStyle1 = OotdStyle.createOotdStyleBy(style1);

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                List.of(ootdImage),
                Arrays.asList(ootdStyle, ootdStyle1));

        return ootdRepository.save(ootd);
    }

    private Clothes createClothesBy(User user, boolean isOpen, String idx) {

        Brand brand = Brand.builder().name("브랜드" + idx).build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리" + idx, SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리" + idx, savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈" + idx).lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색" + idx).colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(savedColor));

        Clothes clothes = Clothes.createClothes(user, savedBrand, "구매처" + idx, PurchaseStoreType.Write, "제품명" + idx,
                isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private void makeAuthenticatedUserBy(User user) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = OAuthUtils.createJwtAuthentication(user);
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
