package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
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
}
