package zip.ootd.ootdzip.clothes.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Data
@NoArgsConstructor
public class SaveClothesRes {

    private Long id;

    public SaveClothesRes(Long id) {
        this.id = id;
    }

    public static SaveClothesRes of(Clothes clothes) {
        return new SaveClothesRes(clothes.getId());
    }
}
