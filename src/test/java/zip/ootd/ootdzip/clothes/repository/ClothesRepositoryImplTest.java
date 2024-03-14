package zip.ootd.ootdzip.clothes.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class ClothesRepositoryImplTest extends IntegrationTestSupport {

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

    @DisplayName("옷을 검색한다.")
    @Test
    void searchClothesBy() {
        // given
        User user = createUserBy("유저1");

        Brand brand1 = createBrandBy("브랜드1");
        Brand brand2 = createBrandBy("브랜드2");
        Category parentCategory1 = createParentCategoryBy("상위카테고리1", SizeType.TOP);
        Category category1 = createCategoryBy("하위카테고리1", parentCategory1, SizeType.TOP);
        Size size = createSizeBy("사이즈1", SizeType.TOP);
        Color color1 = createColorBy("색1", "#fffff1");

        Clothes clothes1 = createClothesBy("옷1", brand1, category1, size, List.of(color1), true, user);
        Clothes clothes2 = createClothesBy("옷2", brand2, category1, size, List.of(color1), true, user);

        // when
        Slice<FindClothesRes> result = clothesRepository.searchClothesBy(user.getId(),
                user.getId(),
                true,
                List.of(brand1.getId()),
                List.of(category1.getId()),
                List.of(color1.getId()),
                Pageable.ofSize(10));
        //then
        assertThat(result).hasSize(1)
                .extracting("id")
                .containsExactlyInAnyOrder(clothes1.getId());

    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }

    private Brand createBrandBy(String brandName) {
        Brand brand = Brand.builder()
                .name(brandName)
                .build();
        return brandRepository.save(brand);
    }

    private Category createCategoryBy(String categoryName, Category parentCategory, SizeType sizeType) {
        Category category = Category.createDetailCategoryBy(categoryName, parentCategory, sizeType);
        return categoryRepository.save(category);
    }

    private Category createParentCategoryBy(String categoryName, SizeType sizeType) {
        Category category = Category.createLargeCategoryBy(categoryName, sizeType);
        return categoryRepository.save(category);
    }

    private Size createSizeBy(String name, SizeType sizeType) {
        Size size = Size.builder()
                .name(name)
                .lineNo((byte)1)
                .sizeType(sizeType)
                .build();
        return sizeRepository.save(size);
    }

    private Color createColorBy(String name, String colorCode) {
        Color color = Color.builder()
                .name(name)
                .colorCode(colorCode)
                .build();
        return colorRepository.save(color);
    }

    private Clothes createClothesBy(String clothesName,
            Brand brand,
            Category category,
            Size size,
            List<Color> colors,
            Boolean isPrivate,
            User user) {

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(colors);

        Clothes clothes = Clothes.createClothes(user,
                brand,
                "구매처",
                PurchaseStoreType.Write,
                clothesName,
                isPrivate,
                category,
                size,
                "메모",
                "구매기간",
                "Image.jpg",
                clothesColors);

        return clothesRepository.save(clothes);
    }

}
