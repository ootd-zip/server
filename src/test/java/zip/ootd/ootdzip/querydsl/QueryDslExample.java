package zip.ootd.ootdzip.querydsl;

import static zip.ootd.ootdzip.clothes.domain.QClothes.*;
import static zip.ootd.ootdzip.ootd.domain.QOotd.*;
import static zip.ootd.ootdzip.ootdimage.domain.QOotdImage.*;
import static zip.ootd.ootdzip.ootdimageclothe.domain.QOotdImageClothes.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.jpa.impl.JPAQueryFactory;

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
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.ootdstyle.domain.QOotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class QueryDslExample extends IntegrationTestSupport {
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

    @Autowired
    private JPAQueryFactory queryFactory;

    @DisplayName("OOTD를 검색한다")
    @Test
    void searchOotds() {
        // given
        User user1 = createUserBy("유저1");
        for (int i = 0; i < 10; i++) {

            Clothes clothes1 = createClothesBy(user1, true, "1");
            Clothes clothes2 = createClothesBy(user1, true, "2");
            Style style = createStyleBy("스타일1");

            Ootd ootd = createOotdBy(user1, "내용본문", false, List.of(clothes1, clothes2), List.of(style));
        }
        // when

        List<Ootd> result = queryFactory.selectFrom(ootd)
                .innerJoin(ootd.styles, QOotdStyle.ootdStyle)
                .innerJoin(ootd.ootdImages, ootdImage)
                .leftJoin(ootdImage.ootdImageClothesList, ootdImageClothes)
                .on(ootdImageClothes.clothes.isPrivate.eq(false),
                        ootdImageClothes.clothes.reportCount.loe(5))
                .innerJoin(ootdImageClothes.clothes, clothes)
                .fetch();
        //then
    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate, List<Clothes> clothesList,
            List<Style> styles) {

        List<OotdImageClothes> ootdImageClothes = new ArrayList<>();

        for (Clothes clothes : clothesList) {
            Coordinate coordinate = new Coordinate("22.33", "33.44");
            DeviceSize deviceSize = new DeviceSize(100L, 50L);

            ootdImageClothes.add(OotdImageClothes.builder().clothes(clothes)
                    .coordinate(coordinate)
                    .deviceSize(deviceSize)
                    .build());
        }

        OotdImage ootdImage = OotdImage.createOotdImageBy(
                "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png",
                ootdImageClothes);

        List<OotdStyle> ootdStyles = new ArrayList<>();

        for (Style style : styles) {
            ootdStyles.add(OotdStyle.createOotdStyleBy(style));
        }

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                List.of(ootdImage),
                ootdStyles);

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
                !isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "image" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private Style createStyleBy(String name) {
        Style style = Style.builder().name(name).build();
        return styleRepository.save(style);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        user.setGender(UserGender.MALE);
        return userRepository.save(user);
    }
}
