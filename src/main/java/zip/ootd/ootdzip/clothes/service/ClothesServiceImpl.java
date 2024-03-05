package zip.ootd.ootdzip.clothes.service;

import static zip.ootd.ootdzip.common.exception.code.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;
import zip.ootd.ootdzip.clothes.service.request.SearchClothesSvcReq;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesIsPrivateSvcReq;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.utils.ImageFileUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClothesServiceImpl implements ClothesService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final ClothesRepository clothesRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    @Override
    @Transactional
    public SaveClothesRes saveClothes(SaveClothesSvcReq request, User loginUser) {

        /*
        옷 관련 도메인 조회
         */
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_BRAND_ID));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_CATEGORY_ID));
        List<Color> colors = colorRepository.findAllById(request.getColorIds());
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_SIZE_ID));

        if (colors.size() != request.getColorIds().size()
                || request.getColorIds().isEmpty()) {
            throw new CustomException(NOT_FOUND_COLOR_ID);
        }

        if (!category.getType().equals(CategoryType.DetailCategory)) {
            throw new CustomException(REQUIRED_DETAIL_CATEGORY);
        }

        if (!size.getSizeType().equals(category.getSizeType())) {
            throw new CustomException(INVALID_CATEGORY_AND_SIZE);
        }

        if (!ImageFileUtil.isValidImageUrl(request.getClothesImageUrl())) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(colors);

        Clothes clothes = Clothes.createClothes(loginUser,
                brand,
                request.getPurchaseStore(),
                request.getPurchaseStoreType(),
                request.getName(),
                request.getIsPrivate(),
                category,
                size,
                request.getMemo(),
                request.getPurchaseDate(),
                request.getClothesImageUrl(),
                clothesColors);
        Clothes saveClothes = clothesRepository.save(clothes);

        return SaveClothesRes.of(saveClothes);
    }

    @Override
    public FindClothesRes findClothesById(Long id, User loginUser) {

        Clothes clothes = clothesRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_CLOTHES_ID));

        if (clothes.getIsPrivate() && !clothes.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }

        return FindClothesRes.of(clothes);
    }

    @Override
    public List<FindClothesRes> findClothesByUser(SearchClothesSvcReq request, User loginUser) {

        List<FindClothesRes> result = new ArrayList<>();
        List<Clothes> clothesList;

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER_ID));

        if (user.equals(loginUser)) {
            clothesList = clothesRepository.findByUser(user, request.getPageable());
        } else {
            clothesList = clothesRepository.findByUserAndIsPrivateFalse(user, request.getPageable());
        }

        for (Clothes clothes : clothesList) {
            result.add(FindClothesRes.of(clothes));
        }

        return result;
    }

    @Override
    @Transactional
    public DeleteClothesByIdRes deleteClothesById(Long id, User loginUser) {

        Clothes deleteClothes = clothesRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_CLOTHES_ID));

        /*
        로그인한 유저와 옷을 등록한 유저가 다르면 실패
         */
        if (!deleteClothes.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }

        clothesRepository.delete(deleteClothes);

        return new DeleteClothesByIdRes("옷 삭제 성공");
    }

    @Override
    @Transactional
    public SaveClothesRes updateClothes(UpdateClothesSvcReq request, User loginUser) {

        Clothes updateTarget = clothesRepository.findById(request.getClothesId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_CLOTHES_ID));

        if (!updateTarget.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_BRAND_ID));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_CATEGORY_ID));
        List<Color> colors = colorRepository.findAllById(request.getColorIds());
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_SIZE_ID));

        if (colors.size() != request.getColorIds().size()
                || request.getColorIds().isEmpty()) {
            throw new CustomException(NOT_FOUND_COLOR_ID);
        }

        if (!category.getType().equals(CategoryType.DetailCategory)) {
            throw new CustomException(REQUIRED_DETAIL_CATEGORY);
        }

        if (!size.getSizeType().equals(category.getSizeType())) {
            throw new CustomException(INVALID_CATEGORY_AND_SIZE);
        }

        if (!ImageFileUtil.isValidImageUrl(request.getClothesImageUrl())) {
            throw new CustomException(INVALID_IMAGE_URL);
        }

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(colors);

        updateTarget.updateClothes(brand,
                request.getPurchaseStore(),
                request.getPurchaseStoreType(),
                request.getName(),
                request.getIsPrivate(),
                category,
                size,
                request.getMemo(),
                request.getPurchaseDate(),
                request.getClothesImageUrl(),
                clothesColors);

        Clothes updatedClothes = clothesRepository.save(updateTarget);
        return SaveClothesRes.of(updatedClothes);
    }

    @Override
    @Transactional
    public SaveClothesRes updateClothesIsPrivate(UpdateClothesIsPrivateSvcReq request, User loginUser) {

        Clothes updateTarget = clothesRepository.findById(request.getClothesId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_CLOTHES_ID));

        if (!updateTarget.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }

        updateTarget.updateIsPrivate(request.getIsPrivate());

        Clothes updatedClothes = clothesRepository.save(updateTarget);

        return SaveClothesRes.of(updatedClothes);
    }
}
