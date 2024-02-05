package zip.ootd.ootdzip.comment.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OotdRepository ootdRepository;

    /**
     * 댓글 기능
     * 댓글, 대댓글까지 허용(대대댓글 불가능)
     * 대댓글을 반드시 누구에 대한 대댓글인지 태깅이 명시되어야함
     * Depth 의 경우 댓글이 1, 대댓글이 2
     */
    public void saveComment(CommentPostReq request) {

        User writer = userService.getAuthenticatiedUser();
        Ootd ootd = ootdRepository.findById(request.getOotdId()).orElseThrow();
        Comment comment;

        int parentDepth = request.getParentDepth();
        if (parentDepth == 0) {
            comment = Comment.builder()
                    .writer(writer)
                    .depth(parentDepth + 1)
                    .contents(request.getContent())
                    .build();
            ootd.addComment(comment); // 대댓글이 아닌 댓글만 ootd 에 저장
        } else {
            User taggedUser = null;
            if (request.getTaggedUserName() != null && !request.getTaggedUserName().isEmpty()) {
                taggedUser = userRepository.findByName(request.getTaggedUserName()).orElseThrow();
            }
            Comment parentComment = commentRepository.findById(request.getCommentParentId()).orElseThrow();
            comment = Comment.builder()
                    .writer(writer)
                    .depth(parentDepth + 1)
                    .contents(request.getContent())
                    .taggedUser(taggedUser)
                    .build();
            parentComment.addChildComment(comment); // 대댓글의 경우 ootd 정보를 따로 저장하지 않아 ootd 확인시 부모댓글을 조회해서 ootd 를 확인해야함
        }

        commentRepository.save(comment);
    }

    /**
     * soft_delete 로 삭제합니다.
     * 삭제여부와 삭제시간을 변경합니다.
     * 이미 삭제된 댓글인 경우 예외를 반환합니다.
     */
    public void deleteOotd(Long id) {

        Comment comment = commentRepository.findById(id).orElseThrow();

        if (comment.getIsDeleted()) {
            throw new CustomException(ErrorCode.DUPLICATE_DELETE_COMMENT);
        }

        comment.deleteComment();
    }
}
