package zip.ootd.ootdzip.clothes.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;

class ClothesTest {

    @DisplayName("옷을 생성한다")
    @Test
    void createClothes() {
        // given
        User user = User.builder().name("유저1").gender(UserGender.MALE).build();

        Brand brand = Brand.builder().name("브랜드1").build();

        Category parentCategory = Category.builder().name("카테고리1").type(CategoryType.LargeCategory).build();

        Category category = Category.builder()
                .name("카테고리2")
                .parentCategory(parentCategory)
                .type(CategoryType.DetailCategory)
                .sizeType(SizeType.TOP)
                .build();

        Size size = Size.builder().sizeType(SizeType.TOP).name("XL").lineNo((byte)1).build();

        Color color = Color.builder().name("색1").colorCode("#fffff").build();

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(color));

        // when
        Clothes clothes = Clothes.createClothes(user, brand, "구매처", "제품명", true, category, size, "재질", "구매일",
                "image1.jpg", clothesColors);

        //then
        assertThat(clothes).extracting("user.name", "brand.name", "purchaseStore", "name", "isOpen", "category.name",
                        "size.name", "material", "purchaseDate", "imageUrl")
                .contains("유저1", "브랜드1", "구매처", "제품명", true, "카테고리2", "XL", "재질", "구매일", "image1.jpg");

        assertThat(clothes.getClothesColors()).hasSize(1)
                .extracting("color.name", "color.colorCode")
                .contains(tuple("색1", "#fffff"));

    }
}
