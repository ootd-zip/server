package zip.ootd.ootdzip.clothes.data;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveClothesReq {

    @NotBlank
    private String purchaseStore;

    @NotNull
    @Positive
    private Long brandId;

    @NotNull
    @Positive
    private Long categoryId;

    private List<@Positive Long> colorIds;

    @NotNull
    private Boolean isOpen;

    @NotNull
    private Long sizeId;

    private List<@NotBlank String> clothesImages;

    private String material;

    private String alias;

    private String purchaseDate;

}
