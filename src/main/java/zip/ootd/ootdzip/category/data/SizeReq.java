package zip.ootd.ootdzip.category.data;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SizeReq {

    @Positive(message = "카테고리 ID는 양수여야 한다.")
    private Long categoryId;
}
