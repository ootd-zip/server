package zip.ootd.ootdzip.clothes.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

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

        // when
        List<Clothes> result = clothesRepository.findByUser(savedUser);

        //then
        assertThat(result).hasSize(2)
                .extracting("user.id", "id", "isOpen")
                .containsExactlyInAnyOrder(tuple(savedUser.getId(), savedClothes1.getId(), true),
                        tuple(savedUser.getId(), savedClothes2.getId(), false));
    }

    @DisplayName("해당 유저의 공개된 옷만 조회한다.")
    @Test
    void findByUserAndIsOpenTrue() {
        // given
        User user = User.getDefault();

        User savedUser = userRepository.save(user);

        Clothes clothes1 = createClothesBy(savedUser, true, "1");
        Clothes clothes2 = createClothesBy(savedUser, false, "2");

        Clothes savedClothes1 = clothesRepository.save(clothes1);
        Clothes savedClothes2 = clothesRepository.save(clothes2);

        // when
        List<Clothes> result = clothesRepository.findByUserAndIsOpenTrue(savedUser);

        //then
        assertThat(result).hasSize(1)
                .extracting("user.id", "id", "isOpen")
                .containsExactlyInAnyOrder(tuple(savedUser.getId(), savedClothes1.getId(), true));
    }

    private Clothes createClothesBy(User user, boolean isOpen, String idx) {

        Brand brand = Brand.builder().name("브랜드" + idx).build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.builder().name("부모 카테고리" + idx).type(CategoryType.LargeCategory).build();

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.builder()
                .name("카테고리" + idx)
                .parentCategory(savedParentCategory)
                .type(CategoryType.DetailCategory)
                .build();

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().category(savedCategory).name("사이즈" + idx).lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색" + idx).colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(savedColor));

        Clothes clothes = Clothes.createClothes(user, savedBrand, "구매처" + idx, "제품명" + idx, isOpen, savedCategory,
                savedSize, "재질" + idx, "구매일" + idx, "image" + idx + ".jpg", clothesColors);

        return clothes;
    }

}