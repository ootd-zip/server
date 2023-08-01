package zip.ootd.ootdzip.clothes.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.ClothesResponseDto;
import zip.ootd.ootdzip.clothes.data.ClothesSaveDto;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.domain.ClothesImage;
import zip.ootd.ootdzip.clothes.domain.ClothesStyle;
import zip.ootd.ootdzip.clothes.repository.ClothesColorRepository;
import zip.ootd.ootdzip.clothes.repository.ClothesImageRepository;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.clothes.repository.ClothesStyleRepository;
import zip.ootd.ootdzip.config.S3Config;
import zip.ootd.ootdzip.user.User;
import zip.ootd.ootdzip.user.UserRepository;

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

    private final S3Config s3Config;

    @Override
    @Transactional
    public ClothesResponseDto saveClothes(ClothesSaveDto clothesSaveDto, List<MultipartFile> imageList) {
        ClothesResponseDto result;
        Clothes clothes;
        Optional<Brand> brand;
        Optional<User> user;
        Optional<Category> category;
        List<Style> styleList;
        List<Color> colorList;
        List<String> imageUrlList;
        List<ClothesImage> clothesImageList;
        List<ClothesStyle> clothesStyleList;
        List<ClothesColor> clothesColorList;

        brand       = brandRepository.findById(clothesSaveDto.getBrandId());
        user        = userRepository.findById(clothesSaveDto.getUserId());
        category    = categoryRepository.findById(clothesSaveDto.getCategoryId());
        styleList   = styleRepository.findAllById(clothesSaveDto.getStyleIdList());
        colorList   = colorRepository.findAllById(clothesSaveDto.getColorIdList());

        imageUrlList = s3Config.uploadImageListToS3(imageList);

        clothes = Clothes.builder()
                .user(user.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 User ID")))
                .brand(brand.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Brand ID")))
                .name(clothesSaveDto.getClothesName())
                .isOpen(clothesSaveDto.getIsOpen())
                .category(category.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Category ID")))
                .size(clothesSaveDto.getSize())
                .material(clothesSaveDto.getMaterial())
                .purchaseStore(clothesSaveDto.getPurchaseStore())
                .purchaseDate(clothesSaveDto.getPurchaseDate())
                .build();

        Clothes savedClothes = clothesRepository.save(clothes);

        clothesStyleList = styleList
                .stream()
                .map(x -> ClothesStyle
                    .builder()
                    .style(x)
                    .clothes(savedClothes)
                    .build())
                .toList();

        clothesImageList = imageUrlList
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


        result = ClothesResponseDto.builder()
                .clothesName(savedClothes.getName())
                .brand(new BrandDto(brand.get()))
                .category(categoryRepository.findDetailCategoryById(category.get().getId()))
                .styleList(savedClothesStyleList.stream().map(x -> x.getStyle().getName()).toList())
                .colorList(savedClothesColorList.stream().map(x -> x.getColor().getName()).toList())
                .imageList(savedClothesImageList.stream().map(ClothesImage::getImageUrl).toList())
                .isOpen(savedClothes.getIsOpen())
                .size(savedClothes.getSize())
                .material(savedClothes.getMaterial())
                .purchaseStore(savedClothes.getPurchaseStore())
                .purchaseDate(savedClothes.getPurchaseDate())
                .build();

        return result;
    }
}
