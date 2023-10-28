package zip.ootd.ootdzip.clothes.service;

import java.util.List;

import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.FindClothesByUserReq;
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
     * @param id
     * @return
     */
    FindClothesRes findClothesById(Long id);

    /**
     * 유저가 등록한 옷 리스트 조회
     * @param request
     * @return
     */
    List<FindClothesRes> findClothesByUser(FindClothesByUserReq request);

    /**
     * id로 옷 정보 삭제
     * @param id
     * @return
     */
    DeleteClothesByIdRes deleteClothesById(Long id);
}
