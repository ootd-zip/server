package zip.ootd.ootdzip.clothes.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.ClothesResponseDto;
import zip.ootd.ootdzip.clothes.data.SaveClothesDto;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.domain.ClothesImage;
import zip.ootd.ootdzip.clothes.domain.ClothesStyle;
import zip.ootd.ootdzip.clothes.repository.ClothesColorRepository;
import zip.ootd.ootdzip.clothes.repository.ClothesImageRepository;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.clothes.repository.ClothesStyleRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService{

    private final ClothesRepository clothesRepository;
    private final ClothesStyleRepository clothesStyleRepository;
    private final ClothesImageRepository clothesImageRepository;
    private final ClothesColorRepository clothesColorRepository;

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final StyleRepository styleRepository;
    private final ColorRepository colorRepository;

    //private final S3Config s3Config;

    @Override
    @Transactional
    public ClothesResponseDto saveClothes(SaveClothesDto saveClothesDto) {
        ClothesResponseDto result;
        Clothes clothes;
        Optional<Brand> brand;
        Optional<User> user;
        Optional<Category> category;
        List<Style> styleList;
        List<Color> colorList;
        List<ClothesImage> clothesImageList;
        List<ClothesStyle> clothesStyleList;
        List<ClothesColor> clothesColorList;

        /*
        옷 관련 도메인 조회
         */
        brand       = brandRepository.findById(saveClothesDto.getBrandId());
        user        = userRepository.findById(saveClothesDto.getUserId());
        category    = categoryRepository.findById(saveClothesDto.getCategoryId());
        styleList   = styleRepository.findAllById(saveClothesDto.getStyleIdList());
        colorList   = colorRepository.findAllById(saveClothesDto.getColorIdList());

        /*
        옷 저장
         */
        clothes = Clothes.builder()
                .user(user.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 User ID")))
                .brand(brand.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Brand ID")))
                .name(saveClothesDto.getClothesName())
                .isOpen(saveClothesDto.getIsOpen())
                .category(category.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Category ID")))
                .size(saveClothesDto.getSize())
                .material(saveClothesDto.getMaterial())
                .purchaseStore(saveClothesDto.getPurchaseStore())
                .purchaseDate(saveClothesDto.getPurchaseDate())
                .build();

        Clothes savedClothes = clothesRepository.save(clothes);

        /*
        옷 관련 1:N 데이터 저장
         */
        clothesStyleList = styleList
                .stream()
                .map(x -> ClothesStyle
                    .builder()
                    .style(x)
                    .clothes(savedClothes)
                    .build())
                .toList();

        clothesImageList = saveClothesDto.getClothesImageList()
                .stream()
                .map(x -> ClothesImage.builder()
                    .imageUrl(x)
                    .clothes(savedClothes)
                    .build())
                .toList();

        clothesColorList = colorList
                .stream()
                .map(x -> ClothesColor.builder()
                    .color(x)
                    .clothes(savedClothes)
                    .build())
                .toList();

        List<ClothesStyle> savedClothesStyleList = clothesStyleRepository.saveAll(clothesStyleList);
        List<ClothesImage> savedClothesImageList = clothesImageRepository.saveAll(clothesImageList);
        List<ClothesColor> savedClothesColorList = clothesColorRepository.saveAll(clothesColorList);

        /*
        옷 저장 결과 DTO 반환(저장 후에 어떤 정보 보내줄지 확인 필요)
         */
        result = ClothesResponseDto.builder()
                .id(clothes.getId())
                .build();

        return result;
    }
}