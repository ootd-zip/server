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
public class SaveClothesReq {

    @NotBlank
    @Size(max = 100)
    private String clothesName;

    @NotNull
    @Positive
    private Long brandId;

    @NotNull
    @Positive
    private Long categoryId;

    private List<@Positive Long> styleIds;

    private List<@Positive Long> colorIds;

    private Boolean isOpen;

    private String size;

    private String material;

    private String purchaseStore;

    private String purchaseDate;

    private List<@NotBlank String> clothesImages;
}
