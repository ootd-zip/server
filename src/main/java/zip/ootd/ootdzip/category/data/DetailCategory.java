package zip.ootd.ootdzip.category.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Category;

@Getter
@NoArgsConstructor
public class DetailCategory {

    private Long id;

    private String categoryName;

    private Long parentCategoryId;

    private String parentCategoryName;

    @Builder
    private DetailCategory(Long id, String categoryName, Long parentCategoryId, String parentCategoryName) {
        this.id = id;
        this.categoryName = categoryName;
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
    }

    public static DetailCategory of(Category category) {
        return DetailCategory.builder()
                .id(category.getId())
                .categoryName(category.getName())
                .parentCategoryId(category.getParentCategoryId())
                .parentCategoryName(category.getParentCategoryName())
                .build();
    }

}
