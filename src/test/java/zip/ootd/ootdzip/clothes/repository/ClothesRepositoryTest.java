package zip.ootd.ootdzip.clothes.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

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
import zip.ootd.ootdzip.clothes.data.ClothesOotdRepoRes;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.images.domain.Images;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Transactional
class ClothesRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @DisplayName("유저가 가진 옷을 조회한다.")
    @Test
    void findByUser() {
        // given
        User user = User.getDefault();

        User savedUser = userRepository.save(user);

        Clothes clothes1 = createClothesBy(savedUser, true, "1");
        Clothes clothes2 = createClothesBy(savedUser, false, "2");

        Clothes savedClothes1 = clothesRepository.save(clothes1);
        Clothes savedClothes2 = clothesRepository.save(clothes2);

        CommonPageRequest pageRequest = new CommonPageRequest();

        // when
        List<Clothes> result = clothesRepository.findByUser(savedUser, pageRequest.toPageable());

        //then
        assertThat(result).hasSize(2)
                .extracting("user.id", "id", "isPrivate")
                .containsExactlyInAnyOrder(tuple(savedUser.getId(), savedClothes1.getId(), true),
                        tuple(savedUser.getId(), savedClothes2.getId(), false));
    }

    @DisplayName("해당 유저의 공개된 옷만 조회한다.")
    @Test
    void findByUserAndIsOpenTrue() {
        // given
        User user = User.getDefault();

        User savedUser = userRepository.save(user);

        Clothes clothes1 = createClothesBy(savedUser, false, "1");
        Clothes clothes2 = createClothesBy(savedUser, true, "2");

        Clothes savedClothes1 = clothesRepository.save(clothes1);
        Clothes savedClothes2 = clothesRepository.save(clothes2);

        CommonPageRequest pageRequest = new CommonPageRequest();

        // when
        List<Clothes> result = clothesRepository.findByUserAndIsPrivateFalse(savedUser, pageRequest.toPageable());

        //then
        assertThat(result).hasSize(1)
                .extracting("user.id", "id", "isPrivate")
                .containsExactlyInAnyOrder(tuple(savedUser.getId(), savedClothes1.getId(), false));
    }

    @DisplayName("OOTD 에 태그된 옷을 조회한다.")
    @Test
    void findClothesOotdResByOotdId() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        Clothes clothes = createClothesBy(user, false, "0");
        Clothes clothes1 = createClothesBy(user, false, "1");
        Clothes clothes2 = createClothesBy(user, false, "2");
        Clothes clothes3 = createClothesBy(user, false, "3");
        Clothes clothes4 = createClothesBy(user, false, "4");
        Clothes clothes5 = createClothesBy(user, false, "5");
        Clothes clothes6 = createClothesBy(user, false, "6");
        Clothes clothes7 = createClothesBy(user, true, "7");
        Clothes clothes8 = createClothesBy(user1, false, "8");

        Ootd ootd = createOotdBy(user, "ootd0", false, Arrays.asList(clothes1, clothes2));
        Ootd ootd1 = createOotdBy(user, "ootd1", false, Arrays.asList(clothes, clothes2, clothes3));

        CommonPageRequest pageRequest = new CommonPageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(4);
        pageRequest.setSortCriteria("createdAt");
        pageRequest.setSortDirection(Sort.Direction.DESC);

        List<Long> clothesIds = clothesRepository.findByOotdId(ootd.getId()).stream()
                .map(BaseEntity::getId)
                .toList();

        // when
        Slice<ClothesOotdRepoRes> clothesOotdResList = clothesRepository.findClothesOotdResByOotdId(user.getId(),
                clothesIds,
                0,
                6);

        Slice<ClothesOotdRepoRes> clothesOotdResList2 = clothesRepository.findClothesOotdResByOotdId(user.getId(),
                clothesIds,
                6,
                6);

        //then
        assertThat(clothesOotdResList)
                .extracting("id", "isTagged")
                .containsExactly(tuple(clothes2.getId(), 1), tuple(clothes1.getId(), 1),
                        tuple(clothes6.getId(), 0), tuple(clothes5.getId(), 0),
                        tuple(clothes4.getId(), 0), tuple(clothes3.getId(), 0));

        assertThat(clothesOotdResList2)
                .extracting("id", "isTagged")
                .containsExactly(tuple(clothes.getId(), 0));
    }

    private Clothes createClothesBy(User user, boolean isPrivate, String idx) {

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
                isPrivate, savedCategory, savedSize, "메모입니다." + idx, "구매일" + idx,
                "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg",
                clothesColors);

        return clothesRepository.save(clothes);
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

        OotdImage ootdImage = OotdImage.createOotdImageBy(
                Images.of("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png"),
                ootdImageClothesList);

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

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        user.setGender(UserGender.MALE);
        return userRepository.save(user);
    }
}
