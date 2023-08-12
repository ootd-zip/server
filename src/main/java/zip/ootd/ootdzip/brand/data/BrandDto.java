package zip.ootd.ootdzip.brand.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {

    private Long id;

    private String name;

    public BrandDto(Brand brand){
        this.id = brand.getId();
        this.name = brand.getName();
    }
}
