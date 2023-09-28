package zip.ootd.ootdzip.clothes.service;

import zip.ootd.ootdzip.clothes.data.ClothesResponseDto;
import zip.ootd.ootdzip.clothes.data.SaveClothesDto;

public interface ClothesService {

    /**
     * 옷을 저장하고, 저장된 옷 ID를 반환한다.
     * @param saveClothesDto
     * @return
     */
    ClothesResponseDto saveClothes(SaveClothesDto saveClothesDto);
}
