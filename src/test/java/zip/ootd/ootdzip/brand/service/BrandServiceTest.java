package zip.ootd.ootdzip.brand.service;

import static org.assertj.core.api.Assertions.*;
import static zip.ootd.ootdzip.clothes.data.PurchaseStoreType.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.brand.service.request.BrandSearchSvcReq;
import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class BrandServiceTest extends IntegrationTestSupport {

    @Autowired
    private BrandService brandService;

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
    private ClothesRepository clothesRepository;

    @DisplayName("브랜드를 조회한다.")
    @Test
    void getBrand() {
        // given
        createBrandBy("브랜드1", "brand1");
        createBrandBy("테스트1", "test1");

        BrandSearchSvcReq request3 = BrandSearchSvcReq.builder()
                .name("브")
                .build();

        // when
        List<BrandDto> brands = brandService.getBrands(request3);

        //then
        assertThat(brands).hasSize(1)
                .extracting("name")
                .contains("브랜드1");
    }

    @DisplayName("영문명으로 브랜드를 조회한다")
    @Test
    void getBrandWithEngName() {
        // given
        Brand brand = createBrandBy("나이키", "NIKE");
        Brand brand2 = createBrandBy("브랜드", "brand");
        BrandSearchSvcReq request = BrandSearchSvcReq.builder()
                .name("NIKE")
                .build();

        // when
        List<BrandDto> result = brandService.getBrands(request);
        //then
        assertThat(result).hasSize(1)
                .extracting("name", "engName")
                .containsExactlyInAnyOrder(tuple(brand.getName(), brand.getEngName()));
    }

    @DisplayName("대소문자 구분없이 영문명으로 브랜드를 조회한다")
    @Test
    void getBrandWithCaseEngName() {
        // given
        Brand brand = createBrandBy("나이키", "NIKE");
        Brand brand2 = createBrandBy("브랜드", "brand");
        BrandSearchSvcReq request = BrandSearchSvcReq.builder()
                .name("Nike")
                .build();

        BrandSearchSvcReq request2 = BrandSearchSvcReq.builder()
                .name("nike")
                .build();

        BrandSearchSvcReq request3 = BrandSearchSvcReq.builder()
                .name("NIKE")
                .build();

        // when
        List<BrandDto> result = brandService.getBrands(request);
        List<BrandDto> result2 = brandService.getBrands(request2);
        List<BrandDto> result3 = brandService.getBrands(request3);
        //then
        assertThat(result).hasSize(1)
                .extracting("name", "engName")
                .containsExactlyInAnyOrder(tuple(brand.getName(), brand.getEngName()));
        assertThat(result2).hasSize(1)
                .extracting("name", "engName")
                .containsExactlyInAnyOrder(tuple(brand.getName(), brand.getEngName()));
        assertThat(result3).hasSize(1)
                .extracting("name", "engName")
                .containsExactlyInAnyOrder(tuple(brand.getName(), brand.getEngName()));

    }

    @DisplayName("유저가 등록한 옷의 브랜드를 조회한다.")
    @Test
    void getUserBrands() {
        // given
        User user = createUserBy("유저1");
        Brand brand1 = createBrandBy("브랜드1", "brand1");
        Brand brand2 = createBrandBy("브랜드2", "brand2");
        createClothesBy(user, brand1, false, "1");
        createClothesBy(user, brand1, false, "1");
        createClothesBy(user, brand2, false, "1");
        // when
        List<BrandDto> result = brandService.getUserBrands(user.getId(), user);

        //then
        assertThat(result).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(brand1.getId(), brand1.getName()),
                        tuple(brand2.getId(), brand2.getName()));
    }

    @DisplayName("다른 유저가 공개로 등록한 옷의 브랜드만 조회한다.")
    @Test
    void getUserBrandsWithDifferentUser() {
        // given
        User user = createUserBy("유저1");
        Brand brand1 = createBrandBy("브랜드1", "brand1");
        Brand brand2 = createBrandBy("브랜드2", "brand2");
        Brand brand3 = createBrandBy("브랜드3", "brand3");
        createClothesBy(user, brand1, false, "1");
        createClothesBy(user, brand1, false, "2");
        createClothesBy(user, brand2, true, "3");

        User user2 = createUserBy("유저2");
        // when
        List<BrandDto> result = brandService.getUserBrands(user.getId(), user2);

        //then
        assertThat(result).hasSize(1)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(brand1.getId(), brand1.getName()));
    }

    @DisplayName("탈퇴한 유저가 등록한 옷의 브랜드를 조회하면 에러가 발생한다")
    @Test
    void getUserBrandsWithDeletedUser() {
        // given
        User user = createUserBy("유저1", true);
        Brand brand1 = createBrandBy("브랜드1", "brand1");
        Brand brand2 = createBrandBy("브랜드2", "brand2");
        createClothesBy(user, brand1, false, "1");
        createClothesBy(user, brand1, false, "1");
        createClothesBy(user, brand2, false, "1");

        // when
        List<BrandDto> result = brandService.getUserBrands(user.getId(), user);

        //then
        assertThat(result).isEmpty();
    }

    private Clothes createClothesBy(User user, Brand brand, boolean isPrivate, String idx) {
        Category parentCategory = Category.createLargeCategoryBy("상위카테고리" + idx, SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리" + idx, savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈" + idx).lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색" + idx).colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(savedColor));

        Clothes clothes = Clothes.createClothes(user, brand, "구매처" + idx, Write, "제품명" + idx,
                isPrivate, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx,
                "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }

    private User createUserBy(String userName, Boolean isDeleted) {
        User user = User.getDefault();
        user.setName(userName);
        user.setIsDeleted(isDeleted);
        return userRepository.save(user);
    }

    private Brand createBrandBy(String brandName, String brandEngName) {
        Brand brand = Brand.builder()
                .name(brandName)
                .engName(brandEngName)
                .build();
        return brandRepository.save(brand);
    }

}
