package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.images.domain.Images;
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

    @Column(nullable = true)
    private String purchaseStore;

    @Enumerated(EnumType.STRING)
    private PurchaseStoreType purchaseStoreType;

    @Column(nullable = false)
    private Boolean isPrivate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "size_id", nullable = true)
    private Size size;

    private String memo;

    private String name;

    private String purchaseDate;

    private Images images;

    private Integer reportCount;

    @Builder.Default
    @OneToMany(mappedBy = "clothes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClothesColor> clothesColors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OotdImageClothes> ootdImageClothesList = new ArrayList<>();

    public static Clothes createClothes(User user,
            Brand brand,
            String purchaseStore,
            PurchaseStoreType purchaseStoreType,
            String name,
            Boolean isPrivate,
            Category category,
            Size size,
            String memo,
            String purchaseDate,
            String imageUrl,
            List<ClothesColor> clothesColors) {

        Clothes clothes = Clothes.builder()
                .user(user)
                .brand(brand)
                .purchaseStore(purchaseStore)
                .purchaseStoreType(purchaseStoreType)
                .name(name)
                .isPrivate(isPrivate)
                .category(category)
                .size(size)
                .memo(memo)
                .purchaseDate(purchaseDate)
                .images(Images.of(imageUrl))
                .reportCount(0)
                .build();

        clothes.addClothesColors(clothesColors);

        return clothes;
    }

    public void updateClothes(Brand brand,
            String purchaseStore,
            PurchaseStoreType purchaseStoreType,
            String name,
            Boolean isPrivate,
            Category category,
            Size size,
            String memo,
            String purchaseDate,
            Images images,
            List<ClothesColor> clothesColors) {
        this.brand = brand;
        this.purchaseStore = purchaseStore;
        this.purchaseStoreType = purchaseStoreType;
        this.name = name;
        this.isPrivate = isPrivate;
        this.category = category;
        this.size = size;
        this.memo = memo;
        this.purchaseDate = purchaseDate;
        this.images = images;
        this.updateClothesColor(clothesColors);
    }

    private void updateClothesColor(List<ClothesColor> clothesColors) {
        this.clothesColors.clear();
        this.addClothesColors(clothesColors);
    }

    private void addClothesColor(ClothesColor color) {
        this.clothesColors.add(color);
        color.setClothes(this);
    }

    private void addClothesColors(List<ClothesColor> colors) {
        colors.forEach(this::addClothesColor);
    }

    public void increaseReportCount() {
        this.reportCount += 1;
    }

    public void updateIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

}
