package zip.ootd.ootdzip.board.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.board.data.BoardOotdPostReq;
import zip.ootd.ootdzip.board.data.BoardOotdPostRes;
import zip.ootd.ootdzip.board.service.BoardService;
import zip.ootd.ootdzip.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Board 컨트롤러", description = "게시글 관련 api")
@RequestMapping("/api/v1/board")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "ootd 게시글 작성", description = "사용자가 ootd 게시글에 작성한 글을 저장하는 api")
    @PostMapping("/ootd")
    public ApiResponse<BoardOotdPostRes> saveOotdPost(@RequestBody @Valid BoardOotdPostReq request) {

        BoardOotdPostRes response = new BoardOotdPostRes(boardService.postOotd(request));

        return new ApiResponse<>(response);
    }

}
