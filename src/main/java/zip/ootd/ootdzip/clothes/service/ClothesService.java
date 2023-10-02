package zip.ootd.ootdzip.clothes.service;

import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesReq;
import zip.ootd.ootdzip.clothes.domain.Clothes;

public interface ClothesService {

    /**
     * 옷을 저장하고, 저장된 옷 ID를 반환한다.
     * @param saveClothesReq
     * @return
     */
    Clothes saveClothes(SaveClothesReq saveClothesReq);

    /**
     * 옷 id로, 옷을 조회한다.
     * @param findClothesReq
     * @return
     */
    FindClothesRes findClothesById(Long id);
}
