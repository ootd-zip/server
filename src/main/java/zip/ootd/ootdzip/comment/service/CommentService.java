package zip.ootd.ootdzip.comment.service;

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.comment.data.CommentGetAllReq;
import zip.ootd.ootdzip.comment.data.CommentGetAllRes;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.event.NotificationEvent;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final OotdRepository ootdRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserBlockRepository userBlockRepository;

    /**
     * 댓글 기능
     * 댓글, 대댓글까지 허용(대대댓글 불가능)
     * 대댓글을 반드시 누구에 대한 대댓글인지 태깅이 명시되어야함
     * Depth 의 경우 댓글이 1, 대댓글이 2
     * 게시물 마다 부모 댓글 기준으로 댓글 그룹 id 를 가진다.
     */
    public Comment saveComment(CommentPostReq request, User writer) {

        Ootd ootd = ootdRepository.findById(request.getOotdId()).orElseThrow();

        Comment comment;

        int parentDepth = request.getParentDepth();

        if (parentDepth == 0) {
            Long maxGroupId = commentRepository.findMaxGroupIdByOotdId(ootd.getId());
            comment = Comment.builder()
                    .writer(writer)
                    .depth(parentDepth + 1)
                    .contents(request.getContent())
                    .ootd(ootd)
                    .groupId(maxGroupId + 1L)
                    .groupOrder(0L)
                    .build();

            notifyOotdComment(ootd.getWriter(), writer, request.getContent(), ootd.getFirstImage(), ootd.getId());
        } else {
            User taggedUser;
            if (request.getTaggedUserName() != null && !request.getTaggedUserName().isEmpty()) {
                taggedUser = userRepository.findByName(request.getTaggedUserName()).orElseThrow();
            } else {
                throw new CustomException(ErrorCode.NO_TAGGING_USER);
            }
            Comment parentComment = commentRepository.findById(request.getCommentParentId()).orElseThrow();
            Long maxGroupOrder = commentRepository.findMaxGroupIdByOotdIdAndGroupOrder(ootd.getId(),
                    parentComment.getGroupId());
            comment = Comment.builder()
                    .writer(writer)
                    .depth(parentDepth + 1)
                    .contents(request.getContent())
                    .taggedUser(taggedUser)
                    .ootd(ootd)
                    .groupId(parentComment.getGroupId())
                    .groupOrder(maxGroupOrder + 1L)
                    .build();
            parentComment.addChildComment(comment);
            notifyTagComment(taggedUser, writer, request.getContent(), ootd.getFirstImage(), ootd.getId());
        }

        return commentRepository.save(comment);
    }

    private void notifyOotdComment(User receiver, User sender, String content, String imageUrl, Long id) {

        if (receiver.equals(sender)) { // OOTD 작성자와 댓글 작성자가 같으면 알람 X
            return;
        }

        eventPublisher.publishEvent(NotificationEvent.builder()
                .receiver(receiver)
                .sender(sender)
                .notificationType(NotificationType.OOTD_COMMENT)
                .goUrl("ootd/" + id)
                .imageUrl(imageUrl)
                .content(content)
                .build());
    }

    private void notifyTagComment(User receiver, User sender, String content, String imageUrl, Long id) {

        if (receiver.equals(sender)) { // 댓글 작성자와 태깅된 유저가 같으면 알람 X
            return;
        }

        eventPublisher.publishEvent(NotificationEvent.builder()
                .receiver(receiver)
                .sender(sender)
                .notificationType(NotificationType.TAG_COMMENT)
                .goUrl("ootd/" + id)
                .imageUrl(imageUrl)
                .content(content)
                .build());
    }

    /**
     * soft_delete 로 삭제합니다.
     * 삭제여부와 삭제시간을 변경합니다.
     * 이미 삭제된 댓글인 경우 예외를 반환합니다.
     */
    public void deleteOotd(Long id) {

        Comment comment = commentRepository.findById(id).orElseThrow();

        userService.checkValidUser(comment.getWriter());

        if (comment.getIsDeleted()) {
            throw new CustomException(ErrorCode.DUPLICATE_DELETE_COMMENT);
        }

        comment.deleteComment();
    }

    public CommonSliceResponse<CommentGetAllRes> getComments(CommentGetAllReq request, User loginUser) {

        Sort sort = Sort.by(
                Sort.Order.asc("groupId"),
                Sort.Order.asc("groupOrder")
        );
        Pageable pageable = request.toPageableWithSort(sort);

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        Slice<Comment> comments = commentRepository.findAllByOotdId(request.getOotdId(), nonAccessibleUserIds,
                pageable);

        List<CommentGetAllRes> commentGetAllResList = comments.stream()
                .map(comment -> CommentGetAllRes.of(comment, nonAccessibleUserIds))
                .toList();

        return new CommonSliceResponse<>(commentGetAllResList, pageable, comments.isLast());
    }
}
