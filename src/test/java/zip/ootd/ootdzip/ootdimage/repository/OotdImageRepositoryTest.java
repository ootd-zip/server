package zip.ootd.ootdzip.ootdimage.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

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
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class OotdImageRepositoryTest extends IntegrationTestSupport {

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
    private OotdImageRepository ootdImageRepository;

    @DisplayName("주어진 스타일을 포함하는 OotdImage 를 가져온다.")
    @Test
    void findByStyles() {
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

        // when
        Slice<OotdImage> result = ootdImageRepository.findByStyles(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
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
        Slice<OotdImage> result = ootdImageRepository.findByStyles(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
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
        Slice<OotdImage> result = ootdImageRepository.findByStyles(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
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
        Slice<OotdImage> result = ootdImageRepository.findByStyles(ootd.getId(),
                Arrays.asList(baseStyle1, baseStyle2),
                pageable);

        // then
        assertThat(result).hasSize(0);
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

        OotdImage ootdImage = OotdImage.createOotdImageBy("input_image_url",
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
                isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "image" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
