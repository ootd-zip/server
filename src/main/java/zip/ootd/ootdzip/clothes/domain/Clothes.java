package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.user.domain.User;

import java.util.ArrayList;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    private String material;

    private String purchaseStore;

    private String purchaseDate;

    @Builder.Default
    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY)
    private List<ClothesImage> clothesImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY)
    private List<ClothesColor> clothesColors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<OotdImageClothes> ootdImageClothesList = new ArrayList<>();

    public static Clothes createClothes(User user,
            Brand brand,
            String name,
            Boolean isOpen,
            Category category,
            Size size,
            String material,
            String purchaseStore,
            String purchaseDate,
            List<ClothesImage> clothesImages,
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

    public void addClothesColor(ClothesColor color) {
        this.clothesColors.add(color);
        color.setClothes(this);
    }

    public void addClothesColors(List<ClothesColor> colors) {
        colors.forEach(this::addClothesColor);
    }

}
