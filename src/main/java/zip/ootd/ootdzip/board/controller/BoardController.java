package zip.ootd.ootdzip.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zip.ootd.ootdzip.board.data.*;
import zip.ootd.ootdzip.board.service.BoardService;
import zip.ootd.ootdzip.common.response.ApiResponse;

import java.util.List;

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

    @Operation(summary = "ootd 게시글 조회", description = "게시판 id 를 주면 해당 id에 해당하는 게시글 반환 api")
    @GetMapping("/ootd")
    public ApiResponse<BoardOotdGetRes> getOotdPost(@Valid BoardOotdGetReq request) {

        BoardOotdGetRes response = boardService.getOotd(request);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 게시글 전체 조회", description = "최신순으로 게시글을 조회 api")
    @GetMapping("/ootds")
    public ApiResponse<List<BoardOotdGetAllRes>> getOotdPosts() {

        List<BoardOotdGetAllRes> response = boardService.getOotds();

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 게시글 좋아요 변경", description = "특정 게시글의 좋아요를 변경하는 api, 좋아요 상태면 취소가 되고 취소상태면 좋아요가 됩니다.")
    @PostMapping("/like")
    public ApiResponse<BoardLikeRes> changeLike(@RequestBody BoardLikeReq request) {

        BoardLikeRes response = new BoardLikeRes(boardService.changeLike(request));

        return new ApiResponse<>(response);
    }
}