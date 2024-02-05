package zip.ootd.ootdzip.comment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.service.CommentService;
import zip.ootd.ootdzip.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment 컨트롤러", description = "comment 관련 api")
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "comment 작성", description = "사용자가 작성한 comment 를 저장하는 api")
    @PostMapping("")
    public ApiResponse<Boolean> saveComment(@RequestBody @Valid CommentPostReq request) {

        commentService.saveComment(request);

        return new ApiResponse<>(true);
    }
}
