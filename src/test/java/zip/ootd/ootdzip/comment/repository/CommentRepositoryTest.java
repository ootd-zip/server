package zip.ootd.ootdzip.comment.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class CommentRepositoryTest extends IntegrationTestSupport {

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
    private CommentRepository commentRepository;

    /**
     * 댓글 조회시 기본 필터링으로 가져오는 댓글
     * 1. 부모댓글, 삭제X, 신고수 5 미만, 자식댓글없음
     * 2. 부모댓글, 자식댓글존재
     * 3. 자식댓글, 삭제X, 신고수 5 미만
     */
    @DisplayName("댓글 조회시 특정 댓글은 필터링 된다.")
    @Test
    void getComments() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1");
        Comment comment1 = createParentCommentBy(ootd, user1, "hi2");
        Comment comment2 = createParentCommentBy(ootd, user2, "hi3");

        Comment comment3 = createChildCommentBy(comment2, ootd, user2, user, "hello1");
        Comment comment4 = createChildCommentBy(comment2, ootd, user2, user, "hello2");
        Comment comment5 = createChildCommentBy(comment2, ootd, user2, user1, "hello3");

        comment.setReportCount(5); // 필터링됨
        comment1.setIsDeleted(true); // 필터링됨
        comment2.setIsDeleted(true);

        comment3.setReportCount(5); // 필터링됨
        comment4.setIsDeleted(true); // 필터링됨

        // when
        List<Comment> results = commentRepository.findAll();

        // then
        assertThat(results).hasSize(2)
                .extracting("id")
                .contains(comment2.getId(), comment5.getId());
    }

    @DisplayName("선택된 ootd 에서 최대 그룹 id 값 찾기")
    @Test
    void findByMaxGroupId() {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1", 0L);
        Comment comment1 = createParentCommentBy(ootd, user, "hi2", 1L);
        Comment comment2 = createParentCommentBy(ootd, user, "hi3", 2L);

        // when
        Long result = commentRepository.findMaxGroupIdByOotdId(ootd.getId());

        // then
        assertThat(result).isEqualTo(2L);
    }

    @DisplayName("선택된 ootd 에서 최대 그룹 id 값 찾을때 댓글이 없을시 0 반환")
    @Test
    void findByMaxGroupIdInNoComment() {
        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false);

        // when
        Long result = commentRepository.findMaxGroupIdByOotdId(ootd.getId());

        // then
        assertThat(result).isEqualTo(0L);
    }

    @DisplayName("선택된 ootd 에서 최대 그룹 정렬 순서 값 찾기")
    @Test
    void findByMaxGroupOrder() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1", 0L);
        Comment comment1 = createChildCommentBy(comment, ootd, user, user1, "hi2", 0L, 1L);
        Comment comment2 = createChildCommentBy(comment, ootd, user, user1, "hi3", 0L, 2L);

        // when
        Long result = commentRepository.findMaxGroupIdByOotdIdAndGroupOrder(ootd.getId(), 0L);

        // then
        assertThat(result).isEqualTo(2L);
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

    private Comment createChildCommentBy(Comment parentComment, Ootd ootd, User taggedUser, User user, String content) {

        Comment comment = Comment.builder()
                .ootd(ootd)
                .depth(1)
                .writer(user)
                .contents(content)
                .parent(parentComment)
                .taggedUser(taggedUser)
                .groupId(0L)
                .groupOrder(1L)
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
                isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx,
                "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
