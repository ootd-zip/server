package zip.ootd.ootdzip.comment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.data.CommentPostRes;
import zip.ootd.ootdzip.comment.service.CommentService;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment 컨트롤러", description = "comment 관련 api")
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    private final UserService userService;

    @Operation(summary = "comment 작성", description = "사용자가 작성한 comment 를 저장하는 api")
    @PostMapping("")
    public ApiResponse<CommentPostRes> saveComment(@RequestBody @Valid CommentPostReq request) {

        CommentPostRes response = new CommentPostRes(
                commentService.saveComment(request, userService.getAuthenticatiedUser()));

        return new ApiResponse<>(response);
    }

    @Operation(summary = "comment 삭제", description = "사용자가 작성한 comment 를 삭제하는 api 로 soft delete 가 적용 됩니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Long id) {

        commentService.deleteOotd(id);

        return new ApiResponse<>(true);
    }
}
