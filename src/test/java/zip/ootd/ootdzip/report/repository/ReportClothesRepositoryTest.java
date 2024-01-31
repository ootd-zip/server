package zip.ootd.ootdzip.report.repository;

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
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.domain.ReportClothes;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class ReportClothesRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ReportClothesRepository reportClothesRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ColorRepository colorRepository;

    @DisplayName("신고자가 옷을 신고한적이 있다면 true를 반환한다")
    @Test
    void existsByClothesAndReporter() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        Clothes clothes = createClothesBy(user, true, "1");

        ReportClothes reportClothes = ReportClothes.of(report, clothes, reporter);

        reportClothesRepository.save(reportClothes);
        // when
        boolean result = reportClothesRepository.existsByClothesAndReporter(clothes, reporter);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("신고자가 옷을 신고한적이 있다면 false를 반환한다")
    @Test
    void noExistsByClothesAndReporter() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        Clothes clothes = createClothesBy(user, true, "1");

        // when
        boolean result = reportClothesRepository.existsByClothesAndReporter(clothes, reporter);

        //then
        assertThat(result).isFalse();
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }

    private Report createReportBy(String message) {
        Report report = new Report(message);
        return reportRepository.save(report);
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

        return clothesRepository.save(clothes);
    }
}
