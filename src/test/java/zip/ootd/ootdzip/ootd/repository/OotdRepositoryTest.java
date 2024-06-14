package zip.ootd.ootdzip.ootd.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import jakarta.persistence.EntityManager;
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
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class OotdRepositoryTest extends IntegrationTestSupport {

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
    private EntityManager em;

    @DisplayName("단건 조회시 신고 수 5 미만 이어야 한다.")
    @Test
    void findByIdOverReportCount() {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", true);
        ootd.setReportCount(5);
        em.flush();
        em.clear();

        // when
        Optional<Ootd> result = ootdRepository.findById(ootd.getId());

        // then
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @DisplayName("단건 조회시 삭제되지 않은 OOTD 이어야 한다.")
    @Test
    void findByIdNotDeleted() {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", true);
        ootd.setIsDeleted(true);
        em.flush();
        em.clear();

        // when
        Optional<Ootd> result = ootdRepository.findById(ootd.getId());

        // then
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @DisplayName("단건 조회시 차단되지 않은 OOTD 이어야 한다.")
    @Test
    void findByIdNotBlocked() {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", true);
        ootd.setIsBlocked(true);
        em.flush();
        em.clear();

        // when
        Optional<Ootd> result = ootdRepository.findById(ootd.getId());

        // then
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @DisplayName("OOTD 조회시 UserId 에 해당하는 OOTD 이며, OotdId 에 해당하지 않는 OOTD 를 전체조회 한다.")
    @Test
    void findAllByUserIdAndOotdId() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Ootd ootd1 = createOotdBy(user, "안녕1", false);
        Ootd ootd2 = createOotdBy(user, "안녕2", false);
        Ootd ootd3 = createOotdBy(user1, "안녕3", false);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // when
        Slice<Ootd> result = ootdRepository.findAllByUserIdAndOotdId(user.getId(), ootd2.getId(),
                pageable);

        // then
        assertThat(result)
                .hasSize(2)
                .extracting("id")
                .containsExactlyInAnyOrder(ootd.getId(), ootd1.getId());

    }

    @DisplayName("OOTD 조회시 UserId 에 해당하는 OOTD 이며, OotdId 에 해당하지 않는 OOTD 를 전체조회, 두번째 페이지를 조회 한다.")
    @Test
    void findAllByUserIdAndOotdIdWithPageOne() {
        // given
        User user = createUserBy("유저");
        List<Ootd> ootds = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Ootd ootd = createOotdBy(user, "안녕" + i, false);
            ootds.add(ootd);
        }
        // 총 15개 ootd 생성, 1개 제외하고 2페이지 조회시 4개가 나와야함

        int page = 1;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // when
        Slice<Ootd> result = ootdRepository.findAllByUserIdAndOotdId(user.getId(), ootds.get(0).getId(),
                pageable);

        // then
        assertThat(result)
                .hasSize(4)
                .extracting("id")
                .containsExactlyInAnyOrder(ootds.get(1).getId(), ootds.get(2).getId(),
                        ootds.get(3).getId(), ootds.get(4).getId());

    }

    @DisplayName("OOTD 조회시 UserId 에 해당하는 OOTD 이며, OotdId 에 해당하지 않는 OOTD 를 전체조회, 비공개글은 조회하지 않는다.")
    @Test
    void findAllByUserIdAndOotdIdWithoutIsPrivate() {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", true);
        Ootd ootd1 = createOotdBy(user, "안녕1", true);
        Ootd ootd2 = createOotdBy(user, "안녕2", true);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // when
        Slice<Ootd> result = ootdRepository.findAllByUserIdAndOotdId(user.getId(), ootd2.getId(),
                pageable);

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("주어진 스타일을 포함하는 Ootd 를 가져온다.")
    @Test
    void findAllByOotdIdAndStyles() {
        // given
        Style style1 = createStyleBy("올드머니");
        Style style2 = createStyleBy("블루코어");
        Style style3 = createStyleBy("고프코어");
        Style style4 = createStyleBy("시티보이");

        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(style1, style2));
        Ootd ootd1 = createOotdBy(user, "안녕1", false, Arrays.asList(style1, style2));
        Ootd ootd2 = createOotdBy(user, "안녕2", false, Arrays.asList(style1, style2));
        Ootd ootd3 = createOotdBy(user1, "안녕3", false, Arrays.asList(style1, style2));

        // 한 개라도 동일한 스타일이 있으면 포함
        Ootd ootd4 = createOotdBy(user1, "안녕4", false, Arrays.asList(style1));
        Ootd ootd5 = createOotdBy(user1, "안녕5", false, Arrays.asList(style2));
        Ootd ootd6 = createOotdBy(user1, "안녕6", false, Arrays.asList(style1, style3));

        // 포함되는 스타일이 하나도 없을시 포함하지 않음
        Ootd ootd7 = createOotdBy(user1, "안녕7", false, Arrays.asList(style3, style4));

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Style baseStyle1 = ootd.getStyles().get(0).getStyle();
        Style baseStyle2 = ootd.getStyles().get(1).getStyle();

        HashSet<Long> userIds = new HashSet<>();
        userIds.add(0L);

        // when
        Slice<Ootd> result = ootdRepository.findAllByOotdIdNotAndStylesWriterIdNotIn(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
                userIds,
                pageable);

        // then
        assertThat(result).hasSize(6);
    }

    @DisplayName("주어진 스타일을 포함하는 OotdImage 를 가져올 때 신고 수는 5회 미만인 OOTD 이어야 한다.")
    @Test
    void findByStylesOverReportCount() {
        // given
        Style style1 = createStyleBy("올드머니");
        Style style2 = createStyleBy("블루코어");

        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(style1, style2));
        Ootd ootd1 = createOotdBy(user, "안녕1", false, Arrays.asList(style1, style2));
        ootd1.setReportCount(5);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Style baseStyle1 = ootd.getStyles().get(0).getStyle();
        Style baseStyle2 = ootd.getStyles().get(1).getStyle();

        // when
        Slice<Ootd> result = ootdRepository.findAllByOotdIdNotAndStylesWriterIdNotIn(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
                new HashSet<>(),
                pageable);

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("주어진 스타일을 포함하는 OotdImage 를 가져올 때 삭제 되지 않은 OOTD 이어야 한다.")
    @Test
    void findByStylesWithIsDeleted() {
        // given
        Style style1 = createStyleBy("올드머니");
        Style style2 = createStyleBy("블루코어");

        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(style1, style2));
        Ootd ootd1 = createOotdBy(user, "안녕1", false, Arrays.asList(style1, style2));
        ootd1.setIsDeleted(true);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Style baseStyle1 = ootd.getStyles().get(0).getStyle();
        Style baseStyle2 = ootd.getStyles().get(1).getStyle();

        // when
        Slice<Ootd> result = ootdRepository.findAllByOotdIdNotAndStylesWriterIdNotIn(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
                new HashSet<>(),
                pageable);

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("주어진 스타일을 포함하는 OotdImage 를 가져올 때 차단 되지 않은 OOTD 이어야 한다.")
    @Test
    void findByStylesWithIsBlocked() {
        // given
        Style style1 = createStyleBy("올드머니");
        Style style2 = createStyleBy("블루코어");

        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(style1, style2));
        Ootd ootd1 = createOotdBy(user, "안녕1", false, Arrays.asList(style1, style2));
        ootd1.setIsBlocked(true);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Style baseStyle1 = ootd.getStyles().get(0).getStyle();
        Style baseStyle2 = ootd.getStyles().get(1).getStyle();

        // when
        Slice<Ootd> result = ootdRepository.findAllByOotdIdNotAndStylesWriterIdNotIn(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
                new HashSet<>(),
                pageable);

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("OOTD를 검색한다")
    @Test
    void searchOotds() {
        // given
        User user1 = createUserBy("유저1");
        for (int i = 1; i <= 50; i++) {

            Clothes clothes1 = createClothesBy(user1, true, String.valueOf(i));
            Clothes clothes2 = createClothesBy(user1, true, String.valueOf(i + 50));

            Style style = createStyleBy("스타일1");

            Ootd ootd = createOotdBy(user1, String.format("내용본문%d", i), false, List.of(clothes1, clothes2),
                    List.of(style));
        }
        // when
        CommonPageResponse<Ootd> ootds = ootdRepository.searchOotds("3",
                null,
                null,
                null,
                null,
                null,
                OotdSearchSortType.LATEST,
                PageRequest.of(0, 10));
        //then
        assertThat(ootds.getContent()).hasSize(10);
        assertThat(ootds.getTotal()).isEqualTo(14);
        assertThat(ootds.getIsLast()).isFalse();

    }

    @DisplayName("비공개 옷이 등록된 OOTD도 검색된다.")
    @Test
    void searchOotdsWithPrivateClothes() {
        // given
        User user1 = createUserBy("유저1");
        for (int i = 1; i <= 50; i++) {

            Clothes clothes1 = createClothesBy(user1, false, String.valueOf(i));
            Clothes clothes2 = createClothesBy(user1, false, String.valueOf(i + 50));

            Style style = createStyleBy("스타일1");

            Ootd ootd = createOotdBy(user1, String.format("내용본문%d", i), false, List.of(clothes1, clothes2),
                    List.of(style));
        }
        // when
        CommonPageResponse<Ootd> ootds = ootdRepository.searchOotds("3",
                null,
                null,
                null,
                null,
                null,
                OotdSearchSortType.LATEST,
                PageRequest.of(0, 10));
        //then
        assertThat(ootds.getContent()).hasSize(10);
        assertThat(ootds.getTotal()).isEqualTo(14);
        assertThat(ootds.getIsLast()).isFalse();

    }

    @DisplayName("비공개 OOTD는 검색되지 않는다.")
    @Test
    void searchOotdsWithPrivateOotds() {
        // given
        User user1 = createUserBy("유저1");
        for (int i = 1; i <= 10; i++) {

            Clothes clothes1 = createClothesBy(user1, false, String.valueOf(i));
            Clothes clothes2 = createClothesBy(user1, false, String.valueOf(i + 50));

            Style style = createStyleBy("스타일1");

            Ootd ootd = createOotdBy(user1, String.format("내용본문%d", i), i % 2 == 0, List.of(clothes1, clothes2),
                    List.of(style));
        }
        // when
        CommonPageResponse<Ootd> ootds = ootdRepository.searchOotds("",
                null,
                null,
                null,
                null,
                null,
                OotdSearchSortType.LATEST,
                PageRequest.of(0, 10));
        //then
        assertThat(ootds.getContent()).hasSize(5);
        assertThat(ootds.getTotal()).isEqualTo(5);
        assertThat(ootds.getIsLast()).isTrue();

    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate) {

        Clothes clothes = createClothesBy(user, isPrivate, "1");
        Clothes clothes1 = createClothesBy(user, isPrivate, "2");

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

        OotdImage ootdImage = OotdImage.createOotdImageBy(
                "https://ootdzip.s3.ap-northeast-2.amazonaws.com/8c00f7f4-3f47-4238-90e7-0bedfeebcae0_2024-06-14.png",
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
                Arrays.asList(ootdImage),
                Arrays.asList(ootdStyle, ootdStyle1));

        return ootdRepository.save(ootd);
    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate, List<Clothes> clothesList,
            List<Style> styles) {

        List<OotdImageClothes> ootdImageClothes = new ArrayList<>();

        for (Clothes clothes : clothesList) {
            Coordinate coordinate = new Coordinate("22.33", "33.44");
            DeviceSize deviceSize = new DeviceSize(100L, 50L);

            ootdImageClothes.add(OotdImageClothes.builder().clothes(clothes)
                    .coordinate(coordinate)
                    .deviceSize(deviceSize)
                    .build());
        }

        OotdImage ootdImage = OotdImage.createOotdImageBy(
                "https://ootdzip.s3.ap-northeast-2.amazonaws.com/8c00f7f4-3f47-4238-90e7-0bedfeebcae0_2024-06-14.png",
                ootdImageClothes);

        List<OotdStyle> ootdStyles = new ArrayList<>();

        for (Style style : styles) {
            ootdStyles.add(OotdStyle.createOotdStyleBy(style));
        }

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                List.of(ootdImage),
                ootdStyles);

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
                !isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "image" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
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

        OotdImage ootdImage = OotdImage.createOotdImageBy(
                "https://ootdzip.s3.ap-northeast-2.amazonaws.com/8c00f7f4-3f47-4238-90e7-0bedfeebcae0_2024-06-14.png",
                Arrays.asList(ootdImageClothes, ootdImageClothes1));

        List<OotdStyle> ootdStyles = OotdStyle.createOotdStylesBy(styles);

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                Arrays.asList(ootdImage),
                ootdStyles);

        return ootdRepository.save(ootd);
    }

    private Style createStyleBy(String name) {
        Style style = Style.builder().name(name).build();
        return styleRepository.save(style);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        user.setGender(UserGender.MALE);
        return userRepository.save(user);
    }
}
