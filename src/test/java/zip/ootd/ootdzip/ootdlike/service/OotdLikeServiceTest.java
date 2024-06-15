package zip.ootd.ootdzip.ootdlike.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootd.service.OotdService;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdlike.controller.response.OotdLikeRes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class OotdLikeServiceTest extends IntegrationTestSupport {

    @Autowired
    private OotdLikeService ootdLikeService;

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
    private EntityManager em;

    @DisplayName("유저가 좋아요한 ootd를 조회한다")
    @Test
    void getUserOotdLikes() {
        // given
        User user1 = createUserBy("유저1");
        User loginUser = createUserBy("로그인유저");

        Ootd ootd1 = createOotdBy(user1, "내용1", false);
        Ootd ootd2 = createOotdBy(user1, "내용2", false);

        ootdService.addLike(ootd1.getId(), loginUser);
        ootdService.addLike(ootd2.getId(), loginUser);
        // when
        List<OotdLikeRes> result = ootdLikeService.getUserOotdLikes(loginUser);

        //then
        assertThat(result).hasSize(2)
                .extracting("ootdId", "ootdImageUrl", "ootdImageCount", "writerId", "writerProfileImage", "writerName")
                .containsExactlyInAnyOrder(
                        tuple(ootd1.getId(), ootd1.getFirstImage(), ootd1.getImageCount(), user1.getId(),
                                user1.getProfileImage(), user1.getName()),
                        tuple(ootd2.getId(), ootd2.getFirstImage(), ootd2.getImageCount(), user1.getId(),
                                user1.getProfileImage(), user1.getName()));

    }

    @DisplayName("유저가 좋아요한 ootd를 조회할 때 공개로 설정한 ootd만 조회한다.")
    @Test
    void getUserOotdLikesWithPublicOotd() {
        // given
        User user1 = createUserBy("유저1");
        User loginUser = createUserBy("로그인유저");

        Ootd ootd1 = createOotdBy(user1, "내용1", false);
        Ootd ootd2 = createOotdBy(user1, "내용2", true);

        ootdService.addLike(ootd1.getId(), loginUser);
        ootdService.addLike(ootd2.getId(), loginUser);
        // when
        List<OotdLikeRes> result = ootdLikeService.getUserOotdLikes(loginUser);

        //then
        assertThat(result).hasSize(1)
                .extracting("ootdId", "ootdImageUrl", "ootdImageCount", "writerId", "writerProfileImage", "writerName")
                .containsExactlyInAnyOrder(
                        tuple(ootd1.getId(), ootd1.getFirstImage(), ootd1.getImageCount(), user1.getId(),
                                user1.getProfileImage(), user1.getName()));

    }

    @DisplayName("유저가 좋아요한 ootd를 조회할 때 좋아요한 ootd가 없으면 빈 리스트를 반환한다.")
    @Test
    void getUserOotdLikesWithEmptyResult() {
        // given
        User user1 = createUserBy("유저1");
        User loginUser = createUserBy("로그인유저");

        Ootd ootd1 = createOotdBy(user1, "내용1", false);
        Ootd ootd2 = createOotdBy(user1, "내용2", true);

        // when
        List<OotdLikeRes> result = ootdLikeService.getUserOotdLikes(loginUser);

        //then
        assertThat(result).isEmpty();
    }

    @DisplayName("유저가 좋아요한 ootd를 조회할 때 삭제되지 않은 ootd만 조회한다.")
    @Test
    void getUserOotdLikesWithNotDeletedOotd() {
        // given
        User user1 = createUserBy("유저1");
        User loginUser = createUserBy("로그인유저");

        Ootd ootd1 = createOotdBy(user1, "내용1", false);
        Ootd ootd2 = createOotdBy(user1, "내용2", false);

        ootdService.addLike(ootd1.getId(), loginUser);
        ootdService.addLike(ootd2.getId(), loginUser);

        ootd2.setIsDeleted(true);
        ootdRepository.save(ootd2);
        // when
        List<OotdLikeRes> result = ootdLikeService.getUserOotdLikes(loginUser);

        //then
        assertThat(result).hasSize(1)
                .extracting("ootdId", "ootdImageUrl", "ootdImageCount", "writerId", "writerProfileImage", "writerName")
                .containsExactlyInAnyOrder(
                        tuple(ootd1.getId(), ootd1.getFirstImage(), ootd1.getImageCount(), user1.getId(),
                                user1.getProfileImage(), user1.getName()));

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

        OotdImage ootdImage = OotdImage.createOotdImageBy(
                "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png",
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
