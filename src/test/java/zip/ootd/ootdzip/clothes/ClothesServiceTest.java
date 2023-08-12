package zip.ootd.ootdzip.clothes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import zip.ootd.ootdzip.brand.data.BrandStatus;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.ClothesResponseDto;
import zip.ootd.ootdzip.clothes.data.SaveClothesDto;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.clothes.service.ClothesService;
import zip.ootd.ootdzip.user.User;
import zip.ootd.ootdzip.user.UserGender;
import zip.ootd.ootdzip.user.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ClothesServiceTest {
    @Autowired
    private ClothesService clothesService;
    @Autowired
    private ClothesRepository clothesRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private StyleRepository styleRepository;

    @Test
    public void 옷저장_성공() throws Exception {
        //Given(준비)
        Brand brand = Brand
                .builder()
                .name("브랜드1")
                .status(BrandStatus.Used)
                .build();

        User user = User
                .builder()
                .name("유저1")
                .gender(UserGender.MALE)
                .birthdate(LocalDate.of(1999,1,1))
                .height(170)
                .weight(70)
                .isOpenBody(true)
                .profileImage("imageUrl")
                .isDeleted(false)
                .build();

        Category largeCategory = Category
                .builder()
                .name("카테고리1")
                .type(CategoryType.LargeCategory)
                .build();

        Category savedLargeCategory = categoryRepository.save(largeCategory);

        Category middleCategory = Category
                .builder()
                .name("카테고리2")
                .type(CategoryType.MiddleCategory)
                .parentCategory(savedLargeCategory)
                .build();

        Category savedMiddleCategory = categoryRepository.save(middleCategory);

        Category detailCategory = Category
                .builder()
                .name("카테고리3")
                .type(CategoryType.DetailCategory)
                .parentCategory(savedMiddleCategory)
                .build();

        Category savedDetailCategory = categoryRepository.save(detailCategory);

        Color color = Color
                .builder()
                .name("색1")
                .build();

        Style style = Style
                .builder()
                .name("스타일1")
                .build();

        Brand savedBrand    = brandRepository.save(brand);
        User savedUser      = userRepository.save(user);
        Color savedColor    = colorRepository.save(color);
        Style savedStyle    = styleRepository.save(style);

        List<Long> styleIdList = new ArrayList<>();
        styleIdList.add(savedStyle.getId());

        List<Long> colorIdList = new ArrayList<>();
        colorIdList.add(savedColor.getId());

        List<String> imageList = new ArrayList<>();
        imageList.add("https://url/urlTest");

        SaveClothesDto saveClothesDto = new SaveClothesDto(savedUser.getId(), "옷1", savedBrand.getId(), savedDetailCategory.getId(), styleIdList, colorIdList, true, "사이즈1", "재질1", "상품구입처1", "구매일", imageList);

        //When(실행)
        ClothesResponseDto savedClothes = clothesService.saveClothes(saveClothesDto);

        //Then(검증)
        Optional<Clothes> findClothes = clothesRepository.findById(savedClothes.getId());

        assertThat(findClothes.isEmpty()).isFalse();

        assertThat(findClothes.orElse(new Clothes()).getName()).isEqualTo("옷1");
        assertThat(findClothes.orElse(new Clothes()).getBrand().getName()).isEqualTo(brand.getName());
        assertThat(findClothes.orElse(new Clothes()).getCategory().getName()).isEqualTo(savedDetailCategory.getName());
    }

    @Test
    public void 옷저장_실패_유효하지않은브랜드() throws Exception{
        //Given(준비)
        Brand brand = Brand
                .builder()
                .name("브랜드2")
                .status(BrandStatus.Used)
                .build();

        User user = User
                .builder()
                .name("유저2")
                .gender(UserGender.MALE)
                .birthdate(LocalDate.of(1999,1,1))
                .height(170)
                .weight(70)
                .isOpenBody(true)
                .profileImage("imageUrl")
                .isDeleted(false)
                .build();

        Category largeCategory = Category
                .builder()
                .name("카테고리4")
                .type(CategoryType.LargeCategory)
                .build();

        Category savedLargeCategory = categoryRepository.save(largeCategory);

        Category middleCategory = Category
                .builder()
                .name("카테고리5")
                .type(CategoryType.MiddleCategory)
                .parentCategory(savedLargeCategory)
                .build();

        Category savedMiddleCategory = categoryRepository.save(middleCategory);

        Category detailCategory = Category
                .builder()
                .name("카테고리6")
                .type(CategoryType.DetailCategory)
                .parentCategory(savedMiddleCategory)
                .build();

        Category savedDetailCategory = categoryRepository.save(detailCategory);

        Color color = Color
                .builder()
                .name("색2")
                .build();

        Style style = Style
                .builder()
                .name("스타일2")
                .build();

        Brand savedBrand    = brandRepository.save(brand);
        User savedUser      = userRepository.save(user);
        Color savedColor    = colorRepository.save(color);
        Style savedStyle    = styleRepository.save(style);

        List<Long> styleIdList = new ArrayList<>();
        styleIdList.add(savedStyle.getId());

        List<Long> colorIdList = new ArrayList<>();
        colorIdList.add(savedColor.getId());

        List<String> imageList = new ArrayList<>();
        imageList.add("https://url/urlTest");

        //When(실행)
        SaveClothesDto saveClothesDto = new SaveClothesDto(savedUser.getId(), "옷1", savedBrand.getId() + 300L, savedDetailCategory.getId(), styleIdList, colorIdList, true, "사이즈1", "재질1", "상품구입처1", "구매일", imageList);


        //Then(검증)
        assertThatIllegalArgumentException()
                .isThrownBy(() -> clothesService.saveClothes(saveClothesDto))
                .withMessage("유효하지 않은 Brand ID");

    }
}
