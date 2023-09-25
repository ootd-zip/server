package zip.ootd.ootdzip.clothes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import zip.ootd.ootdzip.clothes.service.ClothesServiceImpl;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClothesServiceTest {

    @InjectMocks
    private ClothesServiceImpl clothesService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ColorRepository colorRepository;
    @Mock
    private StyleRepository styleRepository;
    @Mock
    private UserService userService;

    @Test
    public void 옷저장_성공() throws Exception {
        //Given(준비)
        SaveClothesReq saveClothesReq = new SaveClothesReq("옷1", 1L, 1L, new ArrayList<>(), new ArrayList<>(), true, "사이즈1", "재질1", "상품구입처1", "구매일", new ArrayList<>());

        User user = new User();
        when(userService.getAuthenticatiedUser()).thenReturn(user);

        List<Color> colors = new ArrayList<>();
        colors.add(new Color(1L, "color1", "url"));
        when(colorRepository.findAllById(saveClothesReq.getColorIds())).thenReturn(colors);

        List<Style> styles = new ArrayList<>();
        styles.add(new Style(1L, "style1"));
        when(styleRepository.findAllById(saveClothesReq.getStyleIds())).thenReturn(styles);

        Brand brand = new Brand();
        when(brandRepository.findById(saveClothesReq.getBrandId())).thenReturn(Optional.of(brand));

        Category category = new Category();
        when(categoryRepository.findById(saveClothesReq.getCategoryId())).thenReturn(Optional.of(category));

        //When(실행)
        Clothes savedClothes = clothesService.saveClothes(saveClothesReq);

        //Then(검증)

        assertThat(savedClothes.getName()).isEqualTo("옷1");
        assertThat(savedClothes.getBrand()).isEqualTo(brand);
        assertThat(savedClothes.getUser()).isEqualTo(user);
    }
}
