package zip.ootd.ootdzip.report.service.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.report.service.request.ReportType;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class ReportClothesStrategyTest extends IntegrationTestSupport {

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

    @Autowired
    private ReportClothesStrategy reportClothesStrategy;

    @DisplayName("옷을 신고하면 신고한 옷 Id와 해당 옷의 신고수를 반환한다.")
    @Test
    void reportClothes() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        Clothes clothes = createClothesBy(user, true, "1");

        ReportSvcReq request = ReportSvcReq.of(report.getId(), clothes.getId(), ReportType.CLOTHES);

        // when
        ReportResultRes result = reportClothesStrategy.report(reporter, request);

        //then
        assertThat(result)
                .extracting("id", "reportCount")
                .contains(clothes.getId(), 1);

        Clothes reportedClothes = clothesRepository.findById(clothes.getId()).get();

        assertThat(reportedClothes.getReportCount())
                .isEqualTo(result.getReportCount());
    }

    @DisplayName("유효하지 않은 옷 ID를 신고하면 에러가 발생한다.")
    @Test
    void reportClothesWithInvalidClothesId() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        ReportSvcReq request = ReportSvcReq.of(report.getId(), 1L, ReportType.CLOTHES);

        // when & then
        assertThatThrownBy(() -> reportClothesStrategy.report(user, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C004", "유효하지 않은 옷 ID");
    }

    @DisplayName("유효하지 않은 reportId를 신고하면 에러가 발생한다.")
    @Test
    void reportClothesWithInvalidReportId() {
        // given
        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        Clothes clothes = createClothesBy(user, true, "1");

        ReportSvcReq request = ReportSvcReq.of(1L, clothes.getId(), ReportType.CLOTHES);

        // when & then
        assertThatThrownBy(() -> reportClothesStrategy.report(user, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "R001", "유효하지 않은 신고 ID");
    }

    @DisplayName("같은 사람이 같은 옷을 2번 이상 신고하면 에러가 발생한다.")
    @Test
    void reportClothesWithDuplicateUserAndClothes() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        Clothes clothes = createClothesBy(user, true, "1");

        ReportSvcReq request = ReportSvcReq.of(report.getId(), clothes.getId(), ReportType.CLOTHES);
        reportClothesStrategy.report(reporter, request);

        // when & then
        assertThatThrownBy(() -> reportClothesStrategy.report(reporter, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "R002", "신고는 한번만 가능합니다.");
    }

    @DisplayName("작성자가 신고하면 에러가 발생한다.")
    @Test
    void reportMyClothes() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");

        Clothes clothes = createClothesBy(user, true, "1");

        ReportSvcReq request = ReportSvcReq.of(report.getId(), clothes.getId(), ReportType.CLOTHES);

        // when & then
        assertThatThrownBy(() -> reportClothesStrategy.report(user, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "R003", "작성자는 신고가 불가능합니다.");
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

        Category parentCategory = Category.createLargeCategoryBy("부모 카테고리" + idx, SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리" + idx, savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈" + idx).lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색" + idx).colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(savedColor));

        Clothes clothes = Clothes.createClothes(user, savedBrand, "구매처" + idx, PurchaseStoreType.Write, "제품명" + idx,
                isOpen, savedCategory, savedSize, "재질" + idx, "구매일" + idx, "image" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }
}
