package zip.ootd.ootdzip.clothes.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "clothes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clothes extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isOpen;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String size;

    private String material;

    private String purchaseStore;

    private String purchaseDate;

    @Builder.Default
    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY)
    private List<ClothesImage> clothesImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY)
    private List<ClothesStyle> clothesStyles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY)
    private List<ClothesColor> clothesColors = new ArrayList<>();

    public static Clothes createClothes(User user,
                                        Brand brand,
                                        String name,
                                        Boolean isOpen,
                                        Category category,
                                        String size,
                                        String material,
                                        String purchaseStore,
                                        String purchaseDate,
                                        List<ClothesImage> clothesImages,
                                        List<ClothesStyle> clothesStyles,
                                        List<ClothesColor> clothesColors) {

        Clothes clothes = Clothes.builder()
                .user(user)
                .brand(brand)
                .name(name)
                .isOpen(isOpen)
                .category(category)
                .size(size)
                .material(material)
                .purchaseStore(purchaseStore)
                .purchaseDate(purchaseDate)
                .build();

        clothes.addClothesImages(clothesImages);
        clothes.addClothesStyles(clothesStyles);
        clothes.addClothesColors(clothesColors);

        return clothes;
    }


    public void addClothesImage(ClothesImage image) {
        this.clothesImages.add(image);
        image.setClothes(this);
    }

    public void addClothesImages(List<ClothesImage> images) {
        images.forEach(this::addClothesImage);
    }

    public void addClothesStyle(ClothesStyle style) {
        this.clothesStyles.add(style);
        style.setClothes(this);
    }

    public void addClothesStyles(List<ClothesStyle> styles) {
        styles.forEach(this::addClothesStyle);
    }

    public void addClothesColor(ClothesColor color) {
        this.clothesColors.add(color);
        color.setClothes(this);
    }

    public void addClothesColors(List<ClothesColor> colors) {
        colors.forEach(this::addClothesColor);
    }

}
