package zip.ootd.ootdzip.clothes.data;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveClothesDto {

    @NotNull
    @Positive
    private Long userId;

    @NotBlank
    @Size(max = 100)
    private String clothesName;

    @NotNull
    @Positive
    private Long brandId;

    @NotNull
    @Positive
    private Long categoryId;

    private List<@Positive Long> styleIdList;

    private List<@Positive Long> colorIdList;

    private Boolean isOpen;

    private String size;

    @NotBlank
    private String material;

    @NotBlank
    private String purchaseStore;

    @NotBlank
    private String purchaseDate;

    private List<@NotBlank String> clothesImageList;
}
