package zip.ootd.ootdzip.clothes.service;

import zip.ootd.ootdzip.clothes.controller.response.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.ClothesOotdReq;
import zip.ootd.ootdzip.clothes.data.ClothesOotdRes;
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;
import zip.ootd.ootdzip.clothes.service.request.SearchClothesSvcReq;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesIsPrivateSvcReq;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesSvcReq;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.user.domain.User;

public interface ClothesService {

    /**
     * 옷을 저장한다.
     * @param request
     * @param loginUser
     * @return
     */
    SaveClothesRes saveClothes(SaveClothesSvcReq request, User loginUser);

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
    CommonSliceResponse<FindClothesRes> findClothesByUser(SearchClothesSvcReq request, User loginUser);

    /**
     * id로 옷 정보 삭제
     * @param id
     * @return
     */
    DeleteClothesByIdRes deleteClothesById(Long id, User loginUser);

    /**
     * 옷 수정
     * @param request
     * @param loginUser
     * @return
     */
    SaveClothesRes updateClothes(UpdateClothesSvcReq request, User loginUser);

    SaveClothesRes updateClothesIsPrivate(UpdateClothesIsPrivateSvcReq request, User loginUser);

    CommonSliceResponse<ClothesOotdRes> getClothesOotd(ClothesOotdReq request);
}
