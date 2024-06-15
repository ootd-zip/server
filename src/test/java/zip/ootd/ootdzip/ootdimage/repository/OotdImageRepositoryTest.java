package zip.ootd.ootdzip.ootdimage.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

    @DisplayName("주어진 옷을 사용하는 OotdImage 를 가져온다.")
    @Test
    void findByClothes() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        Clothes clothes = createClothesBy(user, true, "0");
        Clothes clothes1 = createClothesBy(user, true, "1");
        Clothes clothes2 = createClothesBy(user, true, "2");

        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(clothes, clothes1, clothes2));

        // 다른 유저의 옷은 포함되지 않음
        Ootd ootd1 = createOotdBy(user1, "안녕1", false, Arrays.asList(clothes, clothes1, clothes2));

        // 한 개라도 동일한 옷이 있으면 포함
        Ootd ootd2 = createOotdBy(user, "안녕2", false, Arrays.asList(clothes));
        Ootd ootd3 = createOotdBy(user, "안녕3", false, Arrays.asList(clothes, clothes1));
        Ootd ootd4 = createOotdBy(user, "안녕4", false, Arrays.asList(clothes, clothes2));

        // 포함되는 옷이 하나도 없을시 포함하지 않음
        Ootd ootd5 = createOotdBy(user, "안녕5", false, Arrays.asList(clothes1));
        Ootd ootd6 = createOotdBy(user, "안녕6", false, Arrays.asList(clothes1, clothes2));

        // 비공개글이어도 본인이면 포함
        Ootd ootd7 = createOotdBy(user, "안녕7", true, Arrays.asList(clothes, clothes1, clothes2));

        // 차단, 신고수, 삭제된건 포함안함
        Ootd ootd8 = createOotdBy(user, "안녕8", false, Arrays.asList(clothes, clothes1, clothes2));
        ootd8.setIsBlocked(true);
        Ootd ootd9 = createOotdBy(user, "안녕9", false, Arrays.asList(clothes, clothes1, clothes2));
        ootd9.setReportCount(10);
        Ootd ootd10 = createOotdBy(user, "안녕10", false, Arrays.asList(clothes, clothes1, clothes2));
        ootd10.setIsDeleted(true);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        HashSet<Long> userIds = new HashSet<>();
        userIds.add(0L);

        // when
        Slice<OotdImage> results = ootdImageRepository.findByClothesAndUserIdAndLoginUserIdAndWriterIdNotIn(
                user.getId(),
                clothes.getId(),
                userIds,
                pageable);

        // then
        assertThat(results).hasSize(5)
                .extracting("id")
                .containsExactly(ootd7.getId(), ootd4.getId(), ootd3.getId(), ootd2.getId(), ootd.getId());
    }

    @DisplayName("주어진 옷을 사용하는 OotdImage 를 가져올시 본인이 아니면 비공개글은 가져오지 않는다.")
    @Test
    void findByClothesWithIsPrivate() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        Clothes clothes = createClothesBy(user1, true, "0");
        Clothes clothes1 = createClothesBy(user1, true, "1");
        Clothes clothes2 = createClothesBy(user1, true, "2");

        // 다른 유저의 옷은 포함되지 않음
        Ootd ootd = createOotdBy(user, "안녕", false, Arrays.asList(clothes, clothes1, clothes2));

        // 비공개 옷은 포함되지 않음
        Ootd ootd1 = createOotdBy(user1, "안녕1", false, Arrays.asList(clothes, clothes1, clothes2));

        Ootd ootd2 = createOotdBy(user1, "안녕2", true, Arrays.asList(clothes, clothes1, clothes2));

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        HashSet<Long> userIds = new HashSet<>();
        userIds.add(0L);

        // when
        Slice<OotdImage> results = ootdImageRepository.findByClothesAndUserIdAndLoginUserIdAndWriterIdNotIn(
                user.getId(),
                clothes.getId(),
                userIds,
                pageable);

        // then
        assertThat(results).hasSize(1)
                .extracting("id")
                .containsExactly(ootd1.getId());
    }

    @DisplayName("내가 가진 옷과 비슷한 옷이 등록된 ootd를 조회한다.")
    @Test
    void findOotdsFromOotdImageForSCDF() {
        // given
        User searchUser = createUserBy("유저1");
        User ootdWriter = createUserBy("유저2");

        Color color1 = createColorBy("색1");
        Color color2 = createColorBy("색2");

        Category category1 = createDetailCategoryBy("카테고리1");
        Category category2 = createDetailCategoryBy("카테고리2");

        Clothes ootdClothes1 = createClothesBy(ootdWriter, true, "1", List.of(color1, color2), category1);
        Clothes ootdClothes2 = createClothesBy(ootdWriter, true, "2", List.of(color1), category2);
        Clothes ootdClothes3 = createClothesBy(ootdWriter, false, "3", List.of(color1), category1);
        Clothes ootdClothes4 = createClothesBy(ootdWriter, true, "4", List.of(color1), category1);

        Ootd ootd1 = createOotdBy(ootdWriter, "내용1", false, List.of(ootdClothes1));
        Ootd ootd2 = createOotdBy(ootdWriter, "내용2", false, List.of(ootdClothes2));
        Ootd ootd3 = createOotdBy(ootdWriter, "내용3", false, List.of(ootdClothes3));
        Ootd ootd4 = createOotdBy(ootdWriter, "내용4", false, List.of(ootdClothes4));
        // when
        List<Ootd> result = ootdImageRepository.findOotdsFromOotdImageForSCDF(
                List.of(color1.getId(), color2.getId()), category1, searchUser, null, PageRequest.of(0, 10));

        //then
        assertThat(result).hasSize(2)
                .extracting("id")
                .containsExactlyInAnyOrder(
                        ootd1.getId(),
                        ootd4.getId());
    }

    @DisplayName("내가 가진 옷과 비슷한 옷이 등록되고 공개로 등록된 ootd를 조회한다.")
    @Test
    void findNotPrivateOotdsFromOotdImageForSCDF() {
        // given
        User searchUser = createUserBy("유저1");
        User ootdWriter = createUserBy("유저2");

        Color color1 = createColorBy("색1");
        Color color2 = createColorBy("색2");

        Category category1 = createDetailCategoryBy("카테고리1");
        Category category2 = createDetailCategoryBy("카테고리2");

        Clothes ootdClothes1 = createClothesBy(ootdWriter, true, "1", List.of(color1, color2), category1);
        Clothes ootdClothes2 = createClothesBy(ootdWriter, true, "2", List.of(color1), category2);
        Clothes ootdClothes3 = createClothesBy(ootdWriter, false, "3", List.of(color1), category1);
        Clothes ootdClothes4 = createClothesBy(ootdWriter, true, "4", List.of(color1), category1);

        Ootd ootd1 = createOotdBy(ootdWriter, "내용1", false, List.of(ootdClothes1));
        Ootd ootd2 = createOotdBy(ootdWriter, "내용2", false, List.of(ootdClothes2));
        Ootd ootd3 = createOotdBy(ootdWriter, "내용3", false, List.of(ootdClothes3));
        Ootd ootd4 = createOotdBy(ootdWriter, "내용4", true, List.of(ootdClothes4));
        // when
        List<Ootd> result = ootdImageRepository.findOotdsFromOotdImageForSCDF(
                List.of(color1.getId(), color2.getId()), category1, searchUser, null, PageRequest.of(0, 10));

        //then
        assertThat(result).hasSize(1)
                .extracting("id")
                .containsExactlyInAnyOrder(ootd1.getId());

    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate, List<Clothes> clothesList) {

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

        OotdImage ootdImage = OotdImage.createOotdImageBy("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png", ootdImageClothesList);

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
                !isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private Clothes createClothesBy(User user, boolean isOpen, String idx, List<Color> colors, Category category) {

        Brand brand = Brand.builder().name("브랜드" + idx).build();

        Brand savedBrand = brandRepository.save(brand);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈" + idx).lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(colors);

        Clothes clothes = Clothes.createClothes(user, savedBrand, "구매처" + idx, PurchaseStoreType.Write, "제품명" + idx,
                !isOpen, category, savedSize, "메모입니다" + idx, "구매일" + idx, "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private Color createColorBy(String name) {
        Color color = Color.builder()
                .name(name)
                .colorCode("#44444")
                .build();

        return colorRepository.save(color);
    }

    private Category createDetailCategoryBy(String name) {
        Category largeCategory = categoryRepository.save(Category.createLargeCategoryBy(name + "_상위", SizeType.TOP));
        Category detailCategory = Category.createDetailCategoryBy(name, largeCategory, SizeType.TOP);
        return categoryRepository.save(detailCategory);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }

}
