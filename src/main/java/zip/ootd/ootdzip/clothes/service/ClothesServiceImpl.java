package zip.ootd.ootdzip.clothes.service;

import static zip.ootd.ootdzip.common.exception.code.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.FindClothesByUserReq;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesReq;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.domain.ClothesImage;
import zip.ootd.ootdzip.clothes.domain.ClothesStyle;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClothesServiceImpl implements ClothesService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final StyleRepository styleRepository;
    private final ColorRepository colorRepository;
    private final ClothesRepository clothesRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    @Override
    @Transactional
    public Clothes saveClothes(SaveClothesReq saveClothesReq) {

        /*
        옷 관련 도메인 조회
         */
        Brand brand = brandRepository.findById(saveClothesReq.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Brand ID"));
        User user = userService.getAuthenticatiedUser();
        Category category = categoryRepository.findById(saveClothesReq.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Category ID"));
        List<Style> styles = styleRepository.findAllById(saveClothesReq.getStyleIds());
        List<Color> colors = colorRepository.findAllById(saveClothesReq.getColorIds());

        if (styles.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 Style ID");
        }

        if (colors.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 Color ID");
        }

        List<ClothesStyle> clothesStyles = ClothesStyle.createClothesStylesBy(styles);
        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(colors);
        List<ClothesImage> clothesImages = ClothesImage.createClothesImagesBy(saveClothesReq.getClothesImages());

        return Clothes.createClothes(user, brand, saveClothesReq.getClothesName(), saveClothesReq.getIsOpen(), category,
                saveClothesReq.getSize(), saveClothesReq.getMaterial(), saveClothesReq.getPurchaseStore(),
                saveClothesReq.getPurchaseDate(), clothesImages, clothesStyles, clothesColors);
    }

    @Override
    public FindClothesRes findClothesById(Long id) {

        Clothes clothes = clothesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 옷 ID"));

        User findUser = userService.getAuthenticatiedUser();

        if (!clothes.getIsOpen() && !clothes.getUser().getId().equals(findUser.getId())) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }

        DetailCategory detailCategory = categoryRepository.findDetailCategoryById(clothes.getCategory().getId());

        if (detailCategory == null) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 ID");
        }

        return FindClothesRes.createFindClothesRes(clothes, detailCategory);
    }

    @Override
    public List<FindClothesRes> findClothesByUser(FindClothesByUserReq request) {

        //TODO : 테스트 케이스 작성 필요
        List<FindClothesRes> result = new ArrayList<>();
        List<Clothes> clothesList;

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 User ID"));

        User loginUser = userService.getAuthenticatiedUser();

        /*
         * 본인 옷장은 isOpen 관계없이 모든 옷 리스트 조회
         * 본인 옷장이 아닌경우 isOpen이 true인 옷 리스트 조회
         */
        if (user.equals(loginUser)) {
            clothesList = clothesRepository.findByUser(user);
        } else {
            clothesList = clothesRepository.findByUserAndIsOpenTrue(user);
        }

        for (Clothes clothes : clothesList) {
            DetailCategory detailCategory = categoryRepository.findDetailCategoryById(clothes.getCategory().getId());

            result.add(FindClothesRes.createFindClothesRes(clothes, detailCategory));
        }

        return result;
    }
}
