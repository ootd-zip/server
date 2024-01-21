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
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.clothes.service.request.FindClothesByUserSvcReq;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;
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
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final ClothesRepository clothesRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    @Override
    @Transactional
    public Clothes saveClothes(SaveClothesSvcReq request, User loginUser) {

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

        if (colors.isEmpty()) {
            throw new CustomException(NOT_FOUND_COLOR_ID);
        }

        if (!category.getType().equals(CategoryType.DetailCategory)) {
            throw new CustomException(REQUIRED_DETAIL_CATEGORY);
        }

        if (!size.getCategory().getId().equals(category.getId())) {
            throw new CustomException(INVALID_CATEGORY_AND_SIZE);
        }

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(colors);

        Clothes clothes = Clothes.createClothes(loginUser,
                brand,
                request.getPurchaseStore(),
                request.getName(),
                request.getIsOpen(),
                category,
                size,
                request.getMaterial(),
                request.getPurchaseDate(),
                request.getClothesImageUrl(),
                clothesColors);

        return clothesRepository.save(clothes);
    }

    @Override
    public FindClothesRes findClothesById(Long id, User loginUser) {

        Clothes clothes = clothesRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_CLOTHES_ID));

        if (Boolean.FALSE.equals(clothes.getIsOpen()) && !clothes.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }

        return FindClothesRes.of(clothes);
    }

    @Override
    public List<FindClothesRes> findClothesByUser(FindClothesByUserSvcReq request, User loginUser) {

        List<FindClothesRes> result = new ArrayList<>();
        List<Clothes> clothesList;

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER_ID));

        /*
         * 본인 옷장은 isOpen 관계없이 모든 옷 리스트 조회
         * 본인 옷장이 아닌경우 isOpen이 true인 옷 리스트 조회
         */
        if (user.getId().equals(loginUser.getId())) {
            clothesList = clothesRepository.findByUser(user);
        } else {
            clothesList = clothesRepository.findByUserAndIsOpenTrue(user);
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
}
