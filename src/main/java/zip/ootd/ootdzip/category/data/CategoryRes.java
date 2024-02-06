package zip.ootd.ootdzip.category.data;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Category;

@Getter
@NoArgsConstructor
public class CategoryRes {

    private Long id;

    private String name;

    private List<DetailCategory> detailCategories;

    @Builder
    private CategoryRes(Long id, String name, List<DetailCategory> detailCategories) {
        this.id = id;
        this.name = name;
        this.detailCategories = detailCategories;
    }

    public static CategoryRes of(Category largeCategory, List<Category> detailCategories) {
        return CategoryRes.builder()
                .id(largeCategory.getId())
                .name(largeCategory.getName())
                .detailCategories(detailCategories.stream().map(DetailCategory::of).toList())
                .build();
    }

    @Getter
    static class DetailCategory {

        private final Long id;

        private final String name;

        @Builder
        private DetailCategory(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public static DetailCategory of(Category category) {
            return DetailCategory.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();
        }
    }
}
