package zip.ootd.ootdzip.comment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.comment.data.CommentGetAllReq;
import zip.ootd.ootdzip.comment.data.CommentGetAllRes;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.data.CommentPostRes;
import zip.ootd.ootdzip.comment.service.CommentService;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment 컨트롤러", description = "comment 관련 api")
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    private final UserService userService;

    @Operation(summary = "comment 작성", description = "사용자가 작성한 comment 를 저장하는 api")
    @PostMapping("/comment")
    public ApiResponse<CommentPostRes> saveComment(@RequestBody @Valid CommentPostReq request) {

        CommentPostRes response = new CommentPostRes(
                commentService.saveComment(request, userService.getAuthenticatiedUser()));

        return new ApiResponse<>(response);
    }

    @Operation(summary = "comment 삭제", description = "사용자가 작성한 comment 를 삭제하는 api 로 soft delete 가 적용 됩니다.")
    @DeleteMapping("/comment/{id}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Long id) {

        commentService.deleteOotd(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 에 해당 하는 comment 전체 조회", description = "ootd 에 해당하는 댓글을 조회합니다.")
    @GetMapping("/comments")
    public ApiResponse<CommonSliceResponse<CommentGetAllRes>> getComments(@Valid CommentGetAllReq request) {

        CommonSliceResponse<CommentGetAllRes> response = commentService.getComments(request,
                userService.getAuthenticatiedUser());

        return new ApiResponse<>(response);
    }
}
