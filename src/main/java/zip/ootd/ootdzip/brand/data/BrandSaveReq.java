package zip.ootd.ootdzip.brand.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class BrandSaveReq {

    @NotBlank(message = "브랜드 이름을 입력해주세요.")
    public String name;

    @Builder
    public BrandSaveReq(String name) {
        this.name = name;
    }

}
