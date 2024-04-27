package zip.ootd.ootdzip.home.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.home.data.ClothesAndOotdsForHomeRes;
import zip.ootd.ootdzip.home.data.SameClothesDifferentFeelRes;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.repository.OotdImageRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final UserService userService;
    private final ClothesRepository clothesRepository;
    private final OotdRepository ootdRepository;
    private final OotdImageRepository ootdImageRepository;

    /**
     * 아래 조건에 부합하는 옷 랜덤 1개씩 조회
     * 1. 옷장에 등록된 지 N년이 된 옷
     * 2. 옷장에 등록된 옷 중 상세정보(사이즈, 소재, 구매처, 구매시기)가 입력되지 않은 옷
     * 3. 등록된 Ootd 중 옷을 태그하지 않은 Ootd
     */
    public List<ClothesAndOotdsForHomeRes> getClothesAndOotdsForHomeRes() {

        List<ClothesAndOotdsForHomeRes> result = new ArrayList<>();
        User loginUser = userService.getAuthenticatiedUser();

        // 상세정보 입력되지 않은 옷
        Long noDetailClothesCount = clothesRepository.countByUserAndNoDetailInfo(loginUser);

        if (0 < noDetailClothesCount) {
            int idx = Double.valueOf(Math.random() * noDetailClothesCount).intValue();
            Pageable pageable = PageRequest.of(idx, 1);
            clothesRepository.findByUserAndNoDetailInfo(loginUser, pageable)
                    .stream()
                    .findFirst()
                    .ifPresent(clothes -> result.add(
                            new ClothesAndOotdsForHomeRes(clothes, "상세정보가 비었어요! ", "구체적인 정보 입력하기")));

        }

        // 옷장에 등록된 지 N년이 된 옷
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Long nthClothesCount = clothesRepository.countByDate(today, loginUser);

        if (0 < nthClothesCount) {
            int idx = Double.valueOf(Math.random() * nthClothesCount).intValue();
            Pageable pageable = PageRequest.of(idx, 1);
            clothesRepository.findByDate(today, loginUser, pageable)
                    .stream()
                    .findFirst()
                    .ifPresent(clothes -> result.add(
                            new ClothesAndOotdsForHomeRes(clothes, String.format("옷과 만난 지 %d년 되었어요!",
                                    clothes.getCreatedAt().getYear() - today.getYear()), "옷의 상태는 어떤가요?")));
        }

        // 등록된 Ootd 중 옷을 태그하지 않은 Ootd
        // TODO : Ootd 에 여러개의 사진이 들어가고 사진마다 옷이 태그되게 변경되었습니다, 등록된 Ootd 중 옷을 태그하지 않은 Ootd 에 대한 구체적인 조건이 필요합니다.
        // Long noClothesOotdCount = ootdRepository.countByWriterAndOotdClothesListIsNull(loginUser);
        //
        // if (0 < noClothesOotdCount) {
        //     int idx = Double.valueOf(Math.random() * noClothesOotdCount).intValue();
        //     Pageable pageable = PageRequest.of(idx, 1);
        //     ootdRepository.findByWriterAndOotdClothesListIsNull(loginUser, pageable)
        //             .stream()
        //             .findFirst()
        //             .ifPresent(ootd -> result.add(
        //                     new ClothesAndOotdsForHomeRes(ootd, "이 날 어떤 옷을 입었나요?", "OOTD에 의류정보 태그하러 가기")));
        // }

        return result;
    }

    /**
     * 같은 옷 다른 느낌
     * 사용자가 가진 옷장에서 색깔과 카테고리가 일치하는 OotdImage 가 있다면 Ootd 조회수 순으로 해당 Image 를 가져옵니다.
     * 해당 Ootd 는 삭제되지않고, 비공개가 아니고, 신고수가 특정 수 이하이고, 본인이 작성한 Ootd 가 아닌경우가 해당됩니다.
     * <p>
     * 옷의 경우 Slice 적용하여 페이지네이션이 가능하도록 하였습니다.
     */
    public List<SameClothesDifferentFeelRes> getSameClothesDifferentFeel(User loginUser) {

        Pageable clothesPageable = PageRequest.of(0, 10, sortClothesByCreatedAt());
        List<Clothes> userClothes = clothesRepository.findByUser(loginUser, clothesPageable);
        List<SameClothesDifferentFeelRes> result = new ArrayList<>();

        for (Clothes clothes : userClothes) {
            Pageable ootdImagePageable = PageRequest.of(0, 4);

            List<Long> colorIds = clothes.getClothesColors().stream()
                    .map(cc -> cc.getColor().getId())
                    .toList();

            List<Ootd> ootds = ootdImageRepository.findOotdsFromOotdImageForSCDF(
                    colorIds,
                    clothes.getCategory(),
                    loginUser,
                    ootdImagePageable);

            result.add(new SameClothesDifferentFeelRes(clothes, ootds));
        }

        return result;
    }

    private Sort sortClothesByCreatedAt() {
        return Sort.by("createdAt").descending();
    }
}
