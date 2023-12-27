package zip.ootd.ootdzip.brand.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandSaveReq {

    @NotBlank(message = "브랜드 이름을 입력해주세요.")
    public String name;
}
