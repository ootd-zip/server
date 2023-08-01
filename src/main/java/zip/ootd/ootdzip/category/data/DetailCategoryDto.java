package zip.ootd.ootdzip.category.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailCategoryDto {
    private Long id;
    private String categoryName;
    private String middleCategoryName;
    private String largeCategoryName;

}
