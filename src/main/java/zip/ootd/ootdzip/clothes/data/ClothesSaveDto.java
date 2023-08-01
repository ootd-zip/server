package zip.ootd.ootdzip.clothes.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClothesSaveDto {
    private Long userId;
    private String clothesName;
    private Long brandId;
    private Long categoryId;
    private List<Long> styleIdList;
    private List<Long> colorIdList;
    private Boolean isOpen;
    private String size;
    private String material;
    private String purchaseStore;
    private String purchaseDate;
}
