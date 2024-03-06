package zip.ootd.ootdzip.notification.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
import zip.ootd.ootdzip.comment.service.CommentService;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.repository.NotificationRepository;
import zip.ootd.ootdzip.oauth.domain.UserAuthenticationToken;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class NotificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("알람을 정상적으로 저장합니다.")
    @Transactional
    @Test
    void saveNotification() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        // when
        Notification result = notificationService.saveNotification(user1, user, NotificationType.OOTD_COMMENT,
                "hi", "oh", "ho");

        // then
        Notification savedResult = notificationRepository.findById(result.getId()).get();
        assertThat(savedResult).extracting("id", "receiver.id", "sender.id",
                        "content", "imageUrl", "goUrl", "notificationType")
                .contains(result.getId(), user1.getId(), user.getId(), "hi", "oh", "ho", NotificationType.OOTD_COMMENT);
    }

    private Comment createChildCommentBy(Comment parentComment, Ootd ootd, User taggedUser, User user, String content,
            Long groupId, Long groupOrder) {

        Comment comment = Comment.builder()
                .ootd(ootd)
                .depth(1)
                .writer(user)
                .contents(content)
                .parent(parentComment)
                .taggedUser(taggedUser)
                .groupId(groupId)
                .groupOrder(groupOrder)
                .build();

        parentComment.setChildCount(parentComment.getChildCount() + 1);

        return commentRepository.save(comment);
    }

    private Comment createParentCommentBy(Ootd ootd, User user, String content, Long groupId) {

        Comment comment = Comment.builder()
                .ootd(ootd)
                .depth(1)
                .writer(user)
                .contents(content)
                .groupId(groupId)
                .groupOrder(0L)
                .build();

        return commentRepository.save(comment);
    }

    private Comment createParentCommentBy(Ootd ootd, User user, String content) {

        Comment comment = Comment.builder()
                .ootd(ootd)
                .depth(1)
                .writer(user)
                .contents(content)
                .groupId(0L)
                .groupOrder(0L)
                .build();

        return commentRepository.save(comment);
    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate) {

        Clothes clothes = createClothesBy(user, true, "1");
        Clothes clothes1 = createClothesBy(user, true, "2");

        Coordinate coordinate = new Coordinate("22.33", "33.44");
        Coordinate coordinate1 = new Coordinate("33.44", "44.55");

        DeviceSize deviceSize = new DeviceSize(100L, 50L);
        DeviceSize deviceSize1 = new DeviceSize(100L, 50L);

        OotdImageClothes ootdImageClothes = OotdImageClothes.builder().clothes(clothes)
                .coordinate(coordinate)
                .deviceSize(deviceSize)
                .build();

        OotdImageClothes ootdImageClothes1 = OotdImageClothes.builder().clothes(clothes1)
                .coordinate(coordinate1)
                .deviceSize(deviceSize1)
                .build();

        OotdImage ootdImage = OotdImage.createOotdImageBy("input_image_url",
                Arrays.asList(ootdImageClothes, ootdImageClothes1));

        Style style = Style.builder().name("올드머니").build();
        styleRepository.save(style);
        Style style1 = Style.builder().name("블루코어").build();
        styleRepository.save(style1);

        OotdStyle ootdStyle = OotdStyle.createOotdStyleBy(style);
        OotdStyle ootdStyle1 = OotdStyle.createOotdStyleBy(style1);

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                Arrays.asList(ootdImage),
                Arrays.asList(ootdStyle, ootdStyle1));

        return ootdRepository.save(ootd);
    }

    private Clothes createClothesBy(User user, boolean isOpen, String idx) {

        Brand brand = Brand.builder().name("브랜드" + idx).build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리" + idx, SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리" + idx, savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈" + idx).lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색" + idx).colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(savedColor));

        Clothes clothes = Clothes.createClothes(user, savedBrand, "구매처" + idx, PurchaseStoreType.Write, "제품명" + idx,
                isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "image" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private void makeAuthenticatedUserBy(User user) {
        UserAuthenticationToken.UserDetails principal = new UserAuthenticationToken.UserDetails(user.getId());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UserAuthenticationToken(principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
