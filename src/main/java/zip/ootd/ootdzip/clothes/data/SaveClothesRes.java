package zip.ootd.ootdzip.clothes.data;

import lombok.Data;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Data
public class SaveClothesRes {

    private Long id;

    public SaveClothesRes(Clothes clothes) {
        this.id = clothes.getId();
    }
}
