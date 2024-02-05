package zip.ootd.ootdzip.category.data;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SizeReq {

    @Positive(message = "카테고리 ID는 양수여야 한다.")
    private Long categoryId;
}
