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

    @Builder
    private BrandDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static BrandDto of(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }
}
