package zip.ootd.ootdzip.brand.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;

@Getter
@NoArgsConstructor
public class BrandDto {

    private Long id;

    private String name;

    private String engName;

    @Builder
    private BrandDto(Long id, String name, String engName) {
        this.id = id;
        this.name = name;
        this.engName = engName;
    }

    public static BrandDto of(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .engName(brand.getEngName())
                .build();
    }
}
