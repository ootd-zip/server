package zip.ootd.ootdzip.comment.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

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
import zip.ootd.ootdzip.comment.data.CommentGetAllReq;
import zip.ootd.ootdzip.comment.data.CommentGetAllRes;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class CommentServiceTest extends IntegrationTestSupport {

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

    @DisplayName("댓글을 작성")
    @Test
    void saveParentComment() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요");
        commentPostReq.setParentDepth(0);

        // when
        Comment result = commentService.saveComment(commentPostReq, user);

        // then
        Comment savedResult = commentRepository.findById(result.getId()).get();
        assertThat(savedResult).extracting("ootd.id", "contents", "depth", "groupId", "groupOrder")
                .contains(ootd.getId(), "안녕하세요", 1, 0L, 0L);
    }

    @DisplayName("댓글 작성시 존재하는 ootd id 이어야 한다.")
    @Test
    void saveParentCommentWithoutOotd() {
        // given
        User user = createUserBy("유저1");

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(123456789L);
        commentPostReq.setContent("안녕하세요");
        commentPostReq.setParentDepth(0);

        // when & then
        assertThatThrownBy(() -> commentService.saveComment(commentPostReq, user)).isInstanceOf(
                NoSuchElementException.class);
    }

    @DisplayName("대댓글을 작성")
    @Test
    void saveChildComment() {
        // given
        User user = createUserBy("유저1");
        User user1 = createUserBy("유저2");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1", 0L);
        Comment comment1 = createParentCommentBy(ootd, user, "hi2", 1L);

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1");
        commentPostReq.setParentDepth(1);
        commentPostReq.setCommentParentId(comment.getId());
        commentPostReq.setTaggedUserName("유저2");

        CommentPostReq commentPostReq1 = new CommentPostReq();
        commentPostReq1.setOotdId(ootd.getId());
        commentPostReq1.setContent("안녕하세요2");
        commentPostReq1.setParentDepth(1);
        commentPostReq1.setCommentParentId(comment1.getId());
        commentPostReq1.setTaggedUserName("유저2");

        // when
        Comment result = commentService.saveComment(commentPostReq, user);
        Comment result1 = commentService.saveComment(commentPostReq1, user);

        // then
        Comment savedResult = commentRepository.findById(result.getId()).get();
        assertThat(savedResult).extracting("ootd.id", "contents", "depth",
                        "taggedUser.id", "parent.id", "groupId", "groupOrder")
                .contains(ootd.getId(), "안녕하세요1", 2, user1.getId(), comment.getId(), comment.getGroupId(), 1L);

        Comment savedResult1 = commentRepository.findById(result1.getId()).get();
        assertThat(savedResult1).extracting("ootd.id", "contents", "depth",
                        "taggedUser.id", "parent.id", "groupId", "groupOrder")
                .contains(ootd.getId(), "안녕하세요2", 2, user1.getId(), comment1.getId(), comment1.getGroupId(), 1L);
    }

    @DisplayName("대댓글 작성시 태그된 유저에 대한 값이 있어야 한다.")
    @Test
    void saveChildCommentWithoutTaggedUserValue() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1");

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1");
        commentPostReq.setParentDepth(1);
        commentPostReq.setCommentParentId(comment.getId());

        // when & then
        assertThatThrownBy(() -> commentService.saveComment(commentPostReq, user)).isInstanceOf(
                CustomException.class);
    }

    @DisplayName("대댓글 작성시 태그된 유저가 존재하는 유저여야 한다.")
    @Test
    void saveChildCommentWithoutTaggedUser() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1");

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1");
        commentPostReq.setParentDepth(1);
        commentPostReq.setCommentParentId(comment.getId());
        commentPostReq.setTaggedUserName("유저2");

        // when & then
        assertThatThrownBy(() -> commentService.saveComment(commentPostReq, user)).isInstanceOf(
                NoSuchElementException.class);
    }

    @DisplayName("대댓글 작성시 부모댓글이 있어야 한다.")
    @Test
    void saveChildCommentWithoutParentComment() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);

        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(ootd.getId());
        commentPostReq.setContent("안녕하세요1");
        commentPostReq.setParentDepth(1);
        commentPostReq.setCommentParentId(123456789L);
        commentPostReq.setTaggedUserName("유저2");

        // when & then
        assertThatThrownBy(() -> commentService.saveComment(commentPostReq, user)).isInstanceOf(
                NoSuchElementException.class);
    }

    @DisplayName("댓글 삭제")
    @Test
    void delete() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1");

        //when
        commentService.deleteOotd(comment.getId());

        // then
        Comment result = commentRepository.findById(comment.getId()).get();
        assertThat(result.getIsDeleted()).isEqualTo(true);
        assertThat(result.getDeletedAt()).isNotNull();
    }

    @DisplayName("댓글 삭제시 존재하는 댓글이어야 한다.")
    @Test
    void deleteWithoutComment() {

        // when & then
        assertThatThrownBy(() -> commentService.deleteOotd(123456789L)).isInstanceOf(
                NoSuchElementException.class);
    }

    @DisplayName("댓글 삭제시 이미 삭제된 댓글이다.")
    @Test
    void duplicatedDelete() {
        // given
        User user = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi1");

        // when & then
        commentService.deleteOotd(comment.getId());
        assertThatThrownBy(() -> commentService.deleteOotd(comment.getId())).isInstanceOf(
                CustomException.class);
    }

    @DisplayName("특정 ootd 에 해당하는 댓글을 조회한다.")
    @Test
    void getComments() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Ootd ootd = createOotdBy(user, "안녕", false);
        Comment comment = createParentCommentBy(ootd, user, "hi");
        Comment comment1 = createParentCommentBy(ootd, user, "hi1", 1L);
        Comment comment2 = createParentCommentBy(ootd, user, "hi2", 2L);

        Comment comment3 = createChildCommentBy(comment1, ootd, user, user1, "hi3", 1L, 1L);
        Comment comment4 = createChildCommentBy(comment1, ootd, user, user1, "hi4", 1L, 2L);

        Comment comment5 = createChildCommentBy(comment2, ootd, user, user1, "hi5", 2L, 1L);

        Comment comment6 = createChildCommentBy(comment, ootd, user, user1, "hi6", 0L, 1L);

        CommentGetAllReq commentGetAllReq = new CommentGetAllReq();
        commentGetAllReq.setOotdId(ootd.getId());
        commentGetAllReq.setPage(0);
        commentGetAllReq.setSize(10);

        // when
        CommonSliceResponse<CommentGetAllRes> results = commentService.getComments(commentGetAllReq);

        // then 댓글이 0->6->1->3->4->2->5 순으로 정렬되어 조회된다.
        assertThat(results.getContent())
                .hasSize(7)
                .extracting("id")
                .containsExactly(comment.getId(), comment6.getId(), comment1.getId(),
                        comment3.getId(), comment4.getId(), comment2.getId(), comment5.getId());
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

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
