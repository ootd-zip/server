package zip.ootd.ootdzip.category.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailCategory {

    private Long id;

    private String categoryName;

    private String middleCategoryName;

    private String largeCategoryName;
}
