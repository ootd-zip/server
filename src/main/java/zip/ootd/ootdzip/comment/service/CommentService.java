package zip.ootd.ootdzip.comment.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
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
                    .ootd(ootd)
                    .writer(writer)
                    .depth(parentDepth + 1)
                    .contents(request.getContent())
                    .build();
        } else {
            User taggedUser = userRepository.findByName(request.getTaggedUserName()).orElseThrow();
            Comment parentComment = commentRepository.findById(request.getCommentParentId()).orElseThrow();
            comment = Comment.builder()
                    .ootd(ootd)
                    .writer(writer)
                    .depth(parentDepth + 1)
                    .contents(request.getContent())
                    .taggedUser(taggedUser)
                    .build();
            parentComment.addChildComment(comment);
        }

        commentRepository.save(comment);
    }
}
