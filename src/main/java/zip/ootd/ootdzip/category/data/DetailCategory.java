package zip.ootd.ootdzip.category.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailCategory {

    private Long id;

    private String categoryName;

    private String parentCategoryName;
}
