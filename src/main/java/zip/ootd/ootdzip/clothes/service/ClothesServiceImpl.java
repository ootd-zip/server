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
import zip.ootd.ootdzip.clothes.data.SaveClothesReq;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.domain.ClothesImage;
import zip.ootd.ootdzip.clothes.domain.ClothesStyle;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {


    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final StyleRepository styleRepository;
    private final ColorRepository colorRepository;

    private final UserService userService;

    //private final S3Config s3Config;

    @Override
    @Transactional
    public Clothes saveClothes(SaveClothesReq saveClothesReq) {

        /*
        옷 관련 도메인 조회
         */
        Brand brand = brandRepository.findById(saveClothesReq.getBrandId()).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Brand ID"));
        User user = userService.getAuthenticatiedUser();
        Category category = categoryRepository.findById(saveClothesReq.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Category ID"));
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

        Clothes clothes = Clothes.createClothes(user,
                brand,
                saveClothesReq.getClothesName(),
                saveClothesReq.getIsOpen(),
                category,
                saveClothesReq.getSize(),
                saveClothesReq.getMaterial(),
                saveClothesReq.getPurchaseStore(),
                saveClothesReq.getPurchaseDate(),
                clothesImages,
                clothesStyles,
                clothesColors);

        return clothes;
    }
}
