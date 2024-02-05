package zip.ootd.ootdzip.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.data.CategoryType;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    private Category parentCategory;

    @Builder
    private Category(String name, CategoryType type, Category parentCategory) {
        this.name = name;
        this.type = type;
        this.parentCategory = parentCategory;
    }

    public static Category createLargeCategoryBy(String name) {
        return Category.builder()
                .name(name)
                .type(CategoryType.LargeCategory)
                .parentCategory(null)
                .build();
    }

    public static Category createDetailCategoryBy(String name, Category parentCategory) {
        return Category.builder()
                .name(name)
                .type(CategoryType.DetailCategory)
                .parentCategory(parentCategory)
                .build();
    }

}
