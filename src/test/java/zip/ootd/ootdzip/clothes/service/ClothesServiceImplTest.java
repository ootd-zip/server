package zip.ootd.ootdzip.clothes.service;

import static org.assertj.core.api.Assertions.*;
import static zip.ootd.ootdzip.clothes.data.PurchaseStoreType.*;

import java.util.ArrayList;
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
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.clothes.service.request.FindClothesByUserSvcReq;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class ClothesServiceImplTest extends IntegrationTestSupport {

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
    private ClothesService clothesService;

    @Autowired
    private ClothesRepository clothesRepository;

    @DisplayName("유저가 옷을 저장한다.")
    @Test
    void saveClothes() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리1", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리1", savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(Write)
                .brandId(savedBrand.getId())
                .categoryId(savedCategory.getId())
                .colorIds(List.of(savedColor.getId()))
                .isOpen(true)
                .sizeId(savedSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();
        // when
        SaveClothesRes result = clothesService.saveClothes(request, user);

        //then
        Clothes saveResult = clothesRepository.findById(result.getId()).get();

        assertThat(saveResult).extracting("id", "name", "user", "brand", "isOpen", "category", "size", "memo",
                        "purchaseStore", "purchaseDate", "imageUrl", "purchaseStoreType")
                .contains(result.getId(), "제품명1", user, savedBrand, true, savedCategory, savedSize, "메모입니다.", "구매처1",
                        "구매시기1", "image1.jpg", Write);

        assertThat(saveResult.getClothesColors()).hasSize(1)
                .extracting("color.name", "color.colorCode")
                .containsExactlyInAnyOrder(tuple("색1", "#fffff"));
    }

    @DisplayName("유효하지 않은 브랜드 id로 옷을 저장하면 에러가 발생한다.")
    @Test
    void saveClothesWithInvalidBrandId() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리1", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리1", savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .brandId(savedBrand.getId() + 1)
                .categoryId(savedCategory.getId())
                .colorIds(List.of(savedColor.getId()))
                .isOpen(true)
                .sizeId(savedSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();
        // when & then
        assertThatThrownBy(() -> clothesService.saveClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "B003", "유효하지 않은 브랜드 ID");

    }

    @DisplayName("유효하지 않은 카테고리 id로 옷을 저장하면 에러가 발생한다.")
    @Test
    void saveClothesWithInvalidCategoryId() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리1", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리1", savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .brandId(savedBrand.getId())
                .categoryId(savedCategory.getId() + 1)
                .colorIds(List.of(savedColor.getId()))
                .isOpen(true)
                .sizeId(savedSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();
        // when & then
        assertThatThrownBy(() -> clothesService.saveClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C002", "유효하지 않은 카테고리 ID");

    }

    @DisplayName("부모 카테고리 id로 옷을 저장하면 에러가 발생한다.")
    @Test
    void saveClothesWithParentCategoryId() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리", savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .brandId(savedBrand.getId())
                .categoryId(parentCategory.getId())
                .colorIds(List.of(savedColor.getId()))
                .isOpen(true)
                .sizeId(savedSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.saveClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C003", "하위 카테고리를 선택해주세요.");

    }

    @DisplayName("유효하지 않은 색 id로 옷을 저장하면 에러가 발생한다.")
    @Test
    void saveClothesWithInvalidColorId() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리1", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리1", savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .brandId(savedBrand.getId())
                .categoryId(savedCategory.getId())
                .colorIds(List.of(savedColor.getId() + 1))
                .isOpen(true)
                .sizeId(savedSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.saveClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C003", "유효하지 않은 색 ID");

    }

    @DisplayName("색 id 없이 옷을 저장하면 에러가 발생한다.")
    @Test
    void saveClothesWithoutColorIds() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리1", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리1", savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .brandId(savedBrand.getId())
                .categoryId(savedCategory.getId())
                .colorIds(new ArrayList<>())
                .isOpen(true)
                .sizeId(savedSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.saveClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C003", "유효하지 않은 색 ID");

    }

    @DisplayName("유효하지 않은 사이즈 id로 옷을 저장하면 에러가 발생한다.")
    @Test
    void saveClothesWithInvalidSizeId() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리1", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리1", savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .brandId(savedBrand.getId())
                .categoryId(savedCategory.getId())
                .colorIds(List.of(savedColor.getId()))
                .isOpen(true)
                .sizeId(savedSize.getId() + 1)
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.saveClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "S002", "유효하지 않은 사이즈 ID");

    }

    @DisplayName("카테고리에 사이즈 타입이 다른 사이즈 Id로 옷을 저장하면 에러가 발생한다.")
    @Test
    void saveClothesWithSizeIdNotIncludedCategory() {
        // given
        User user = createUserBy("유저1");

        Brand brand = Brand.builder().name("브랜드1").build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리1", SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category1 = Category.createDetailCategoryBy("카테고리1", savedParentCategory, SizeType.TOP);

        Category savedCategory1 = categoryRepository.save(category1);

        Size size = Size.builder().sizeType(SizeType.BOTTOM).name("사이즈1").lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        SaveClothesSvcReq request = SaveClothesSvcReq.builder()
                .purchaseStore("구매처1")
                .brandId(savedBrand.getId())
                .categoryId(savedCategory1.getId())
                .colorIds(List.of(savedColor.getId()))
                .isOpen(true)
                .sizeId(savedSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.saveClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "C002", "카테고리에 속한 사이즈가 아님");
    }

    @DisplayName("옷 ID로 옷을 조회한다")
    @Test
    void findClothesById() {
        // given
        User user1 = createUserBy("유저1");
        Clothes clothes = createClothesBy(user1, true, "1");

        // when
        FindClothesRes result = clothesService.findClothesById(clothes.getId(), user1);

        //then
        assertThat(result).extracting("id", "name", "userName", "isOpen", "memo", "purchaseStore", "purchaseDate",
                        "imageUrl", "purchaseStoreType")
                .contains(clothes.getId(), "제품명1", "유저1", true, "메모입니다1", "구매처1", "구매일1", "image1.jpg", Write);

        assertThat(result.getBrand().getName()).isEqualTo("브랜드1");

        assertThat(result.getCategory()).extracting("categoryName", "parentCategoryName").contains("카테고리1", "상위카테고리1");

        assertThat(result.getSize()).extracting("name", "lineNo").contains("사이즈1", (byte)1);

        assertThat(result.getColors()).hasSize(1).extracting("name").contains("색1");
    }

    @DisplayName("유효하지 않은 옷 ID로 옷을 조회하면 에러가 발생한다.")
    @Test
    void findClothesByIdWithInvlidClothesId() {
        // given
        User user1 = createUserBy("유저1");
        Clothes clothes = createClothesBy(user1, false, "1");

        // when & then
        assertThatThrownBy(() -> clothesService.findClothesById(clothes.getId() + 1, user1)).isInstanceOf(
                        CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C004", "유효하지 않은 옷 ID");
    }

    @DisplayName("공개되지 않은 옷의 ID로 다른 유저가 조회하면 에러가 발생한다.")
    @Test
    void findClothesByIdWithNotOpenId() {
        // given
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        Clothes clothes = createClothesBy(user1, false, "1");

        // when & then
        assertThatThrownBy(() -> clothesService.findClothesById(clothes.getId(), user2)).isInstanceOf(
                        CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(401, "C001", "해당 데이터에 접근할 수 없는 사용자");
    }

    @DisplayName("해당 유저의 옷을 조회한다.")
    @Test
    void findClothesByUser() {
        // given
        User user = createUserBy("유저1");
        Clothes clothes1 = createClothesBy(user, false, "1");
        Clothes clothes2 = createClothesBy(user, false, "2");

        FindClothesByUserSvcReq request = FindClothesByUserSvcReq.builder().userId(user.getId()).build();

        // when
        List<FindClothesRes> result = clothesService.findClothesByUser(request, user);

        //then
        assertThat(result).hasSize(2)
                .extracting("id", "name", "userName", "isOpen", "memo", "purchaseStore", "purchaseDate", "imageUrl",
                        "purchaseStoreType")
                .containsExactlyInAnyOrder(
                        tuple(clothes1.getId(), "제품명1", "유저1", false, "메모입니다1", "구매처1", "구매일1", "image1.jpg", Write),
                        tuple(clothes2.getId(), "제품명2", "유저1", false, "메모입니다2", "구매처2", "구매일2", "image2.jpg", Write));

    }

    @DisplayName("다른 유저의 옷을 조회할 때는 공개된 옷만 조회된다.")
    @Test
    void findClothesByUserWithDifferentUser() {
        // given
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        Clothes clothes1 = createClothesBy(user1, false, "1");
        Clothes clothes2 = createClothesBy(user1, true, "2");

        FindClothesByUserSvcReq request = FindClothesByUserSvcReq.builder().userId(user1.getId()).build();

        // when
        List<FindClothesRes> result = clothesService.findClothesByUser(request, user2);

        //then
        assertThat(result).hasSize(1)
                .extracting("id", "name", "userName", "isOpen", "memo", "purchaseStore", "purchaseDate", "imageUrl",
                        "purchaseStoreType")
                .containsExactlyInAnyOrder(
                        tuple(clothes2.getId(), "제품명2", "유저1", true, "메모입니다2", "구매처2", "구매일2", "image2.jpg", Write));

    }

    @DisplayName("유효하지 않은 유저 ID로 옷을 조회하면 에러가 발생한다.")
    @Test
    void findClothesByUserWithInvalidUser() {
        // given
        User user1 = createUserBy("유저1");
        Clothes clothes1 = createClothesBy(user1, false, "1");
        Clothes clothes2 = createClothesBy(user1, true, "2");

        FindClothesByUserSvcReq request = FindClothesByUserSvcReq.builder().userId(user1.getId() + 1).build();

        // when & then
        assertThatThrownBy(() -> clothesService.findClothesByUser(request, user1)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "U002", "유효하지 않은 유저 ID");
    }

    @DisplayName("옷 ID로 옷을 삭제한다.")
    @Test
    void deleteClothesById() {
        // given
        User user1 = createUserBy("유저1");
        Clothes clothes1 = createClothesBy(user1, true, "1");

        // when
        DeleteClothesByIdRes result = clothesService.deleteClothesById(clothes1.getId(), user1);

        //then
        assertThat(result.getMessage()).isEqualTo("옷 삭제 성공");

    }

    @DisplayName("유효하지 않은 옷 ID로 옷을 삭제하면 에러가 발생한다.")
    @Test
    void deleteClothesByIdWithClothesId() {
        // given
        User user1 = createUserBy("유저1");
        Clothes clothes1 = createClothesBy(user1, true, "1");

        // when & then
        assertThatThrownBy(() -> clothesService.deleteClothesById(clothes1.getId() + 1, user1)).isInstanceOf(
                        CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C004", "유효하지 않은 옷 ID");

    }

    @DisplayName("다른 유저가 옷을 삭제하면 에러가 발생한다.")
    @Test
    void deleteClothesByIdWithDifferentUser() {
        // given
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        Clothes clothes1 = createClothesBy(user1, true, "1");

        // when & then
        assertThatThrownBy(() -> clothesService.deleteClothesById(clothes1.getId(), user2)).isInstanceOf(
                        CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(401, "C001", "해당 데이터에 접근할 수 없는 사용자");

    }

    @DisplayName("옷 ID에 해당하는 옷 정보를 수정한다.")
    @Test
    void updateClothes() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateTarget.getCategory().getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when
        SaveClothesRes response = clothesService.updateClothes(request, user);

        //then
        assertThat(response.getId()).isEqualTo(updateTarget.getId());

        Clothes updatedClothes = clothesRepository.findById(updateTarget.getId()).get();

        assertThat(updatedClothes.getName()).isEqualTo("제품명수정");
    }

    @DisplayName("작성자가 아닌 유저가 옷 정보를 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithDifferentUser() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");
        User user2 = createUserBy("유저1");

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateTarget.getCategory().getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user2)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(401, "C001", "해당 데이터에 접근할 수 없는 사용자");

    }

    @DisplayName("유효하지 않은 옷 ID로 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithInvalidClothesId() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId() + 1)
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateTarget.getCategory().getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C004", "유효하지 않은 옷 ID");

    }

    @DisplayName("유효하지 않은 브랜드 ID로 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithInvalidBrandId() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId() + 1)
                .categoryId(updateTarget.getCategory().getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "B003", "유효하지 않은 브랜드 ID");

    }

    @DisplayName("유효하지 않은 카테고리 ID로 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithInvalidCategoryId() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateTarget.getCategory().getId() + 1)
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C002", "유효하지 않은 카테고리 ID");

    }

    @DisplayName("유효하지 않은 사이즈 ID로 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithInvalidSizeId() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateTarget.getCategory().getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId() + 1)
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "S002", "유효하지 않은 사이즈 ID");

    }

    @DisplayName("유효하지 않은 색 ID로 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithInvalidColorId() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateTarget.getCategory().getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId() + 1))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C003", "유효하지 않은 색 ID");

    }

    @DisplayName("부모 카테고리로 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithParentCategory() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리", SizeType.TOP);
        Category updateParentCategory = categoryRepository.save(parentCategory);

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateParentCategory.getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateTarget.getSize().getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "C003", "하위 카테고리를 선택해주세요.");

    }

    @DisplayName("카테고리에 속하지 않은 사이즈로 수정하면 에러가 발생한다.")
    @Test
    void updateClothesWithInvalidCategoryAndSize() {
        // given
        User user = createUserBy("작성자1");
        Clothes updateTarget = createClothesBy(user, true, "1");

        Size size = Size.builder().sizeType(SizeType.BOTTOM).name("사이즈").lineNo((byte)1).build();

        Size updateSize = sizeRepository.save(size);

        UpdateClothesSvcReq request = UpdateClothesSvcReq.builder()
                .clothesId(updateTarget.getId())
                .purchaseStore("구매처1")
                .brandId(updateTarget.getBrand().getId())
                .categoryId(updateTarget.getCategory().getId())
                .colorIds(List.of(updateTarget.getClothesColors().get(0).getColor().getId()))
                .isOpen(true)
                .sizeId(updateSize.getId())
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .name("제품명수정")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThatThrownBy(() -> clothesService.updateClothes(request, user)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "C002", "카테고리에 속한 사이즈가 아님");

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

        Clothes clothes = Clothes.createClothes(user, savedBrand, "구매처" + idx, Write, "제품명" + idx,
                isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "image" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
