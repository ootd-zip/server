package zip.ootd.ootdzip.clothes.service;

import java.util.List;

import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.service.request.FindClothesByUserSvcReq;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;
import zip.ootd.ootdzip.user.domain.User;

public interface ClothesService {

    /**
     * 옷을 저장한다.
     * @param request
     * @param loginUser
     * @return
     */
    Clothes saveClothes(SaveClothesSvcReq request, User loginUser);

    /**
     * 옷 id로, 옷을 조회한다.
     * @param id
     * @return
     */
    FindClothesRes findClothesById(Long id, User loginUser);

    /**
     * 유저가 등록한 옷 리스트 조회
     * @param request
     * @return
     */
    List<FindClothesRes> findClothesByUser(FindClothesByUserSvcReq request, User loginUser);

    /**
     * id로 옷 정보 삭제
     * @param id
     * @return
     */
    DeleteClothesByIdRes deleteClothesById(Long id, User loginUser);
}
