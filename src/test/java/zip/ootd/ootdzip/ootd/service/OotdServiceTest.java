package zip.ootd.ootdzip.ootd.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.ootd.data.OotdPatchReq;
import zip.ootd.ootdzip.ootd.data.OotdPostReq;
import zip.ootd.ootdzip.ootd.data.OotdPutReq;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class OotdServiceTest extends IntegrationTestSupport {

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

    @DisplayName("OOTD 게시글 저장")
    @Test
    void save() {
        // given
        User user = createUserBy("유저");
        Clothes clothes = createClothesBy(user, true, "1");
        Clothes clothes1 = createClothesBy(user, true, "2");

        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(clothes.getId());
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(clothes1.getId());
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPostReq.OotdImageReq ootdImageReq = new OotdPostReq.OotdImageReq();
        ootdImageReq.setOotdImage("input_image_url");
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        Style style = Style.builder().name("올드머니").build();
        Style savedStyle = styleRepository.save(style);
        Style style1 = Style.builder().name("블루코어").build();
        Style savedStyle1 = styleRepository.save(style1);

        OotdPostReq ootdPostReq = new OotdPostReq();
        ootdPostReq.setIsPrivate(false);
        ootdPostReq.setContent("테스트");
        ootdPostReq.setStyles(Arrays.asList(savedStyle.getId(), savedStyle1.getId()));
        ootdPostReq.setOotdImages(Arrays.asList(ootdImageReq));

        // when
        Ootd result = ootdService.postOotd(ootdPostReq, user);

        // then
        Ootd savedResult = ootdRepository.findById(result.getId()).get();

        assertThat(savedResult).extracting("id", "isPrivate", "contents")
                .contains(result.getId(), false, "테스트");

        assertThat(savedResult.getStyles())
                .hasSize(2)
                .extracting("style.name")
                .containsExactlyInAnyOrder("올드머니", "블루코어");

        assertThat(savedResult.getOotdImages())
                .hasSize(1)
                .extracting("imageUrl")
                .contains("input_image_url");

        assertThat(savedResult.getOotdImages().get(0).getOotdImageClothesList())
                .hasSize(2)
                .extracting("clothes.id", "coordinate.xRate", "coordinate.yRate", "deviceSize.deviceWidth",
                        "deviceSize.deviceHeight")
                .containsExactlyInAnyOrder(
                        tuple(clothes.getId(), "22.33", "33.44", 100L, 50L),
                        tuple(clothes1.getId(), "33.44", "44.55", 100L, 50L)
                );
    }

    @DisplayName("OOTD 게시글과 공개여부를 변경한다.")
    @Test
    void updateContentsAndIsPrivate() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        OotdPatchReq ootdPatchReq = new OotdPatchReq();
        ootdPatchReq.setContent("잘가");
        ootdPatchReq.setIsPrivate(true);
        ootdPatchReq.setId(ootd.getId());

        // when
        ootdService.updateContentsAndIsPrivate(ootdPatchReq);

        // then
        assertThat(ootd).extracting("id", "isPrivate", "contents")
                .contains(ootd.getId(), true, "잘가");
    }

    @DisplayName("OOTD 전체 업데이트를 한다")
    @Test
    void updateAll() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Clothes clothes = createClothesBy(user, true, "3");
        Clothes clothes1 = createClothesBy(user, true, "4");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(clothes.getId());
        clothesTagReq.setDeviceWidth(20L);
        clothesTagReq.setDeviceHeight(30L);
        clothesTagReq.setXRate("11.22");
        clothesTagReq.setYRate("22.33");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(clothes1.getId());
        clothesTagReq1.setDeviceWidth(20L);
        clothesTagReq1.setDeviceHeight(30L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("55.66");

        OotdPutReq.OotdImageReq ootdImageReq = new OotdPutReq.OotdImageReq();
        ootdImageReq.setOotdImage("input_image_url1");
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        Style style = Style.builder().name("아메카제").build();
        Style savedStyle = styleRepository.save(style);
        Style style1 = Style.builder().name("미니멀").build();
        Style savedStyle1 = styleRepository.save(style1);

        OotdPutReq ootdPutReq = new OotdPutReq();
        ootdPutReq.setContent("잘가");
        ootdPutReq.setIsPrivate(true);
        ootdPutReq.setId(ootd.getId());
        ootdPutReq.setOotdImages(Arrays.asList(ootdImageReq));
        ootdPutReq.setStyles(Arrays.asList(savedStyle.getId(), savedStyle1.getId()));

        // when
        ootdService.updateAll(ootdPutReq);

        // then
        Ootd savedResult = ootdRepository.findById(ootd.getId()).get();

        assertThat(savedResult).extracting("id", "isPrivate", "contents")
                .contains(ootd.getId(), true, "잘가");

        assertThat(savedResult.getStyles())
                .hasSize(2)
                .extracting("style.name")
                .containsExactlyInAnyOrder("아메카제", "미니멀");

        assertThat(savedResult.getOotdImages())
                .hasSize(1)
                .extracting("imageUrl")
                .contains("input_image_url1");

        assertThat(savedResult.getOotdImages().get(0).getOotdImageClothesList())
                .hasSize(2)
                .extracting("clothes.id", "coordinate.xRate", "coordinate.yRate", "deviceSize.deviceWidth",
                        "deviceSize.deviceHeight")
                .containsExactlyInAnyOrder(
                        tuple(clothes.getId(), "11.22", "22.33", 20L, 30L),
                        tuple(clothes1.getId(), "33.44", "55.66", 20L, 30L)
                );
    }

    @DisplayName("OOTD 를 삭제한다.")
    @Test
    void delete() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        ootdService.deleteOotd(ootd.getId());
        Optional<Ootd> result = ootdRepository.findById(ootd.getId());

        // then
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @DisplayName("OOTD 를 단건 조회한다.")
    @Test
    void findOne() {
        // given

        // when

        // then

    }

    @DisplayName("OOTD 를 전체 조회한다.")
    @Test
    void findAll() {
        // given

        // when

        // then

    }

    @DisplayName("좋아요를 한다.")
    @Test
    void addLike() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        ootdService.addLike(ootd.getId(), user);
        ootdService.addLike(ootd.getId(), user1);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdLikes())
                .hasSize(2)
                .extracting("user.id")
                .contains(user.getId(), user1.getId());
    }

    @DisplayName("좋아요를 취소한다")
    @Test
    void cancelLike() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        ootdService.addLike(ootd.getId(), user);
        ootdService.addLike(ootd.getId(), user1);

        // when
        ootdService.cancelLike(ootd.getId(), user);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdLikes())
                .hasSize(1)
                .extracting("user.id")
                .contains(user1.getId());
    }

    @DisplayName("OOTD 북마크를 추가한다.")
    @Test
    void addBookmark() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        ootdService.addBookmark(ootd.getId(), user);
        ootdService.addBookmark(ootd.getId(), user1);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdBookmarks())
                .hasSize(2)
                .extracting("user.id")
                .contains(user.getId(), user1.getId());
    }

    @DisplayName("OOTD 북마크를 취소한다.")
    @Test
    void cancelBookmark() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        ootdService.addBookmark(ootd.getId(), user);
        ootdService.addBookmark(ootd.getId(), user1);

        // when
        ootdService.cancelBookmark(ootd.getId(), user);
        Ootd result = ootdRepository.findById(ootd.getId()).get();

        // then
        assertThat(result.getOotdBookmarks())
                .hasSize(1)
                .extracting("user.id")
                .contains(user1.getId());
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

        OotdImage ootdImage = OotdImage.createOotdImageBy("input_image_url",
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
