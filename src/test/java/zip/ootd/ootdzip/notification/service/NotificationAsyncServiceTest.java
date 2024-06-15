package zip.ootd.ootdzip.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

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
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
import zip.ootd.ootdzip.comment.service.CommentService;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.notification.repository.NotificationRepository;
import zip.ootd.ootdzip.oauth.OAuthUtils;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootd.service.OotdService;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.service.UserService;

/**
 * 알람 처리를 위해 IntegrationTestSupport 를 상속받지 않았습니다.
 * IntegrationTestSupport 에는 @Transaction 이 있는데
 * 알람 처리로직 중 부모 트랜잭션이 끝나야 수행되기 때문에 포괄적으로 트랜잭션 처리를 할 수 없습니다.
 * 그래서 트랜잭션으로 DB 를 초기화하지 않고 컨텍스트를 재시작해서 DB 를 초기화합니다.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class NotificationAsyncServiceTest {

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
    private OotdService ootdService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("OOTD 댓글 저장시 해당 댓글 ootd 에 대한 알림이 비동기로 저장 됩니다.")
    @Test
    void saveNotificationBySaveComment() throws InterruptedException {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user1, "안녕", false);

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1231");
        commentPostReq.setParentDepth(0);

        // when
        commentService.saveComment(commentPostReq, user);

        // then
        Thread.sleep(100L); // 비동기 처리 대기시간
        List<Notification> results = notificationRepository.findAll();
        assertEquals(1,
                results.stream()
                        .filter(r -> r.getContent().equals("안녕하세요1231"))
                        .count());
    }

    @DisplayName("OOTD 댓글 저장시 해당 댓글 ootd 에 대한 알림이 비동기로 저장 됩니다.(복수)")
    @Test
    void saveNotificationsBySaveComments() throws InterruptedException {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user1, "안녕", false);

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1231");
        commentPostReq.setParentDepth(0);

        // when
        commentService.saveComment(commentPostReq, user);
        commentService.saveComment(commentPostReq, user);
        commentService.saveComment(commentPostReq, user);

        // then
        Thread.sleep(100L); // 비동기 처리 대기시간
        List<Notification> results = notificationRepository.findAll();

        // r.getSender().equals(user)) 사용시 g선etSender() 의 equals() 함수를 사용하는데 이때 이미 트랜잭션은 종료되어있고,
        // getSender() 의 User 는 영속되지 않은 상태라 해당 함수를 가져올 수 없음(해당 비동기테스트는 트랜잭션을 메소드단위로 적용하지않기때문)
        // 그럼 이 테스트 메소드도 트랜잭션이 닫힌 상태인데 어떻게 r.getSender().getId() 가 가능한가?
        // notification 을 조회할때 sender.id 는 가져오기때문에 에러가 안남. 그래서 다른 필드 ex)sender.name 조회시는 에러가 발생함
        assertEquals(3,
                results.stream()
                        .filter(r -> r.getSender().getId().equals(user.getId()))
                        .count());
    }

    @DisplayName("OOTD 댓글알림을 보낼때 수신자와 송신자가 같으면 보내지 않습니다.")
    @Transactional
    @Test
    void saveNotificationBySameReceiverAndSender() throws InterruptedException {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false);

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1231");
        commentPostReq.setParentDepth(0);

        // when
        commentService.saveComment(commentPostReq, user);

        // then
        Thread.sleep(100L); // 비동기 처리 대기시간
        List<Notification> results = notificationRepository.findAll();
        assertEquals(0,
                results.stream()
                        .filter(r -> r.getSender().equals(user))
                        .count());
    }

    @DisplayName("대댓글 저장시 해당 태깅유저에 대한 알림이 비동기로 저장 됩니다.")
    @Test
    void saveNotificationBySaveChildComment() throws InterruptedException {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1", 0L);

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1231");
        commentPostReq.setParentDepth(1);
        commentPostReq.setCommentParentId(comment.getId());
        commentPostReq.setTaggedUserName(user.getName());

        // when
        commentService.saveComment(commentPostReq, user1);
        commentService.saveComment(commentPostReq, user); // 부모댓글과 자식댓글이 같은 작성자는 패스

        // then
        Thread.sleep(100L); // 비동기 처리 대기시간
        List<Notification> results = notificationRepository.findAll();

        assertEquals(1, (long)results.size());
        assertEquals(1,
                results.stream()
                        .filter(r -> r.getContent().equals("안녕하세요1231"))
                        .count());
    }

    @DisplayName("좋아요 추가시 해당 OOTD 작성자에 대한 알림이 비동기로 저장 됩니다.")
    @Test
    void saveNotificationByAddBookmark() throws InterruptedException {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        ootdService.addLike(ootd.getId(), user1);
        ootdService.addLike(ootd.getId(), user); // ootd 작성자와 좋아요 추가한 사람이 동일시 패스

        // then
        Thread.sleep(100L); // 비동기 처리 대기시간
        List<Notification> results = notificationRepository.findAll();

        assertEquals(1, (long)results.size());
    }

    @DisplayName("팔로우 추가시 팔로우한 사람에 대한 알림이 비동기로 저장 됩니다.")
    @Test
    void saveNotificationByFollow() throws InterruptedException {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        // when
        userService.follow(user.getId(), user1.getId());

        // then
        Thread.sleep(100L); // 비동기 처리 대기시간
        List<Notification> results = notificationRepository.findAll();

        assertThat(results)
                .hasSize(1)
                .extracting("receiver.id", "sender.id")
                .containsExactlyInAnyOrder(tuple(user.getId(), user1.getId()));
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

        OotdImage ootdImage = OotdImage.createOotdImageBy(
                "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png",
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
                List.of(ootdImage),
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
                isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx, "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private void makeAuthenticatedUserBy(User user) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = OAuthUtils.createJwtAuthentication(user);
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
