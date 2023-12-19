package zip.ootd.ootdzip.category.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategorySearch {

    private CategoryType categoryType;

    private Long parentCategoryId;
}
