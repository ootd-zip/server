package zip.ootd.ootdzip.clothes.service;

import static zip.ootd.ootdzip.common.exception.code.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import zip.ootd.ootdzip.clothes.controller.response.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.ClothesOotdRepoRes;
import zip.ootd.ootdzip.clothes.data.ClothesOotdReq;
import zip.ootd.ootdzip.clothes.data.ClothesOotdRes;
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;
import zip.ootd.ootdzip.clothes.service.request.SearchClothesSvcReq;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesIsPrivateSvcReq;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesSvcReq;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.images.domain.Images;
import zip.ootd.ootdzip.images.service.ImagesService;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;

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
    private final UserBlockRepository userBlockRepository;
    private final ImagesService imagesService;

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
        Size size = null;

        if (colors.size() != request.getColorIds().size()
                || request.getColorIds().isEmpty()) {
            throw new CustomException(NOT_FOUND_COLOR_ID);
        }

        if (!category.getType().equals(CategoryType.DetailCategory)) {
            throw new CustomException(REQUIRED_DETAIL_CATEGORY);
        }

        if (request.getSizeId() != null
                && 0 < request.getSizeId()) {
            size = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() -> new CustomException(NOT_FOUND_SIZE_ID));

            if (!size.getSizeType().equals(category.getSizeType())) {
                throw new CustomException(INVALID_CATEGORY_AND_SIZE);
            }
        }

        if (null != request.getPurchaseStore()
                && !request.getPurchaseStore().isBlank()
                && null == request.getPurchaseStoreType()) {
            throw new CustomException(INVALID_PURCHASE_STORE_TYPE);
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

        if (clothes.getUser().getIsDeleted()) {
            throw new CustomException(DELETE_USER_CLOTHES);
        }

        if (clothes.getIsPrivate() && !clothes.getUser().equals(loginUser)) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }

        if (userBlockRepository.existUserBlock(clothes.getUser().getId(), loginUser.getId())) {
            throw new CustomException(BLOCK_USER_CONTENTS);
        }

        return FindClothesRes.of(clothes);
    }

    @Override
    public CommonSliceResponse<FindClothesRes> findClothesByUser(SearchClothesSvcReq request, User loginUser) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER_ID));

        if (user.getIsDeleted()) {
            throw new CustomException(DELETE_USER_CLOTHES);
        }

        if (userBlockRepository.existUserBlock(user.getId(), loginUser.getId())) {
            throw new CustomException(BLOCK_USER_CONTENTS);
        }

        Boolean isPrivate = request.getIsPrivate();
        if (!user.equals(loginUser)) {
            isPrivate = false;
        }

        Slice<FindClothesRes> result = clothesRepository.searchClothesBy(loginUser.getId(),
                request.getUserId(),
                isPrivate,
                request.getBrandIds(),
                request.getCategoryIds(),
                request.getColorIds(),
                request.getSearchText(),
                request.getPageable());

        return new CommonSliceResponse<>(result.getContent(), request.getPageable(), result.isLast());
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

        // 삭제전 s3 에서 이미지 삭제
        imagesService.deleteImagesByUrlToS3(deleteClothes.getImages());

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
        Size size = null;

        if (colors.size() != request.getColorIds().size()
                || request.getColorIds().isEmpty()) {
            throw new CustomException(NOT_FOUND_COLOR_ID);
        }

        if (!category.getType().equals(CategoryType.DetailCategory)) {
            throw new CustomException(REQUIRED_DETAIL_CATEGORY);
        }

        if (request.getSizeId() != null
                && 0 < request.getSizeId()) {
            size = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() -> new CustomException(NOT_FOUND_SIZE_ID));

            if (!size.getSizeType().equals(category.getSizeType())) {
                throw new CustomException(INVALID_CATEGORY_AND_SIZE);
            }
        }

        if (null != request.getPurchaseStore()
                && !request.getPurchaseStore().isBlank()
                && null == request.getPurchaseStoreType()) {
            throw new CustomException(INVALID_PURCHASE_STORE_TYPE);
        }

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(colors);

        // 업데이트전 s3 에서 이미지 삭제
        Images images = Images.of(request.getClothesImageUrl());
        if (!updateTarget.getImages().equals(images)) {
            imagesService.deleteImagesByUrlToS3(updateTarget.getImages());
        }

        updateTarget.updateClothes(brand,
                request.getPurchaseStore(),
                request.getPurchaseStoreType(),
                request.getName(),
                request.getIsPrivate(),
                category,
                size,
                request.getMemo(),
                request.getPurchaseDate(),
                images,
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

    @Override
    @Transactional
    public CommonSliceResponse<ClothesOotdRes> getClothesOotd(ClothesOotdReq request, User loginUser) {

        if (userBlockRepository.existUserBlock(request.getUserId(), loginUser.getId())) {
            throw new CustomException(BLOCK_USER_CONTENTS);
        }

        Pageable pageable = request.toPageable();
        List<Long> clothesIds = clothesRepository.findByOotdId(request.getOotdId()).stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());

        Slice<ClothesOotdRepoRes> taggedClothesSlice = clothesRepository.findClothesOotdResByOotdId(request.getUserId(),
                clothesIds,
                request.getPage() * request.getSize(),
                request.getSize());

        List<ClothesOotdRes> clothesOotdResList = taggedClothesSlice.stream()
                .map(ClothesOotdRes::of)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(clothesOotdResList, pageable, taggedClothesSlice.isLast());
    }
}
