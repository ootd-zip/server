package zip.ootd.ootdzip.category.data;

import lombok.Data;

@Data
public class CategorySearch {

    private CategoryType categoryType;

    private Long parentCategoryId;
}
