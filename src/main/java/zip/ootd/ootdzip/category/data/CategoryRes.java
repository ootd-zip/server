package zip.ootd.ootdzip.category.data;

import lombok.Data;
import zip.ootd.ootdzip.category.domain.Category;

@Data
public class CategoryRes {

    private Long id;

    private String name;

    private CategoryType type;

    public CategoryRes(Category category) {

        this.id = category.getId();
        this.name = category.getName();
        this.type = category.getType();
    }

    public static CategoryRes createCategoryResBy(Category category) {
        return new CategoryRes(category);
    }
}
