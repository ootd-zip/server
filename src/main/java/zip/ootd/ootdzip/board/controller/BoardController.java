package zip.ootd.ootdzip.board.controller;

import java.util.List;

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
import zip.ootd.ootdzip.board.data.BoardOotdGetAllRes;
import zip.ootd.ootdzip.board.data.BoardOotdGetRes;
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

    @Operation(summary = "ootd 게시글 조회", description = "게시판 id 를 주면 해당 id에 해당하는 게시글 반환 api")
    @GetMapping("/ootd/{id}")
    public ApiResponse<BoardOotdGetRes> getOotdPost(@PathVariable Long id) {

        BoardOotdGetRes response = boardService.getOotd(id);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 본인 게시글 조회", description = "게시판 id 를 주면 해당 id에 해당하는 게시글 공개여부 상관없이 반환 api")
    @GetMapping("/ootd/mine/{id}")
    public ApiResponse<BoardOotdGetRes> getOotdPostMine(@PathVariable Long id) {

        BoardOotdGetRes response = boardService.getOotdInMine(id);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 게시글 전체 조회", description = "최신순으로 게시글을 조회 api")
    @GetMapping("/ootds")
    public ApiResponse<List<BoardOotdGetAllRes>> getOotdPosts() {

        List<BoardOotdGetAllRes> response = boardService.getOotds();

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 게시글 좋아요 추가", description = "특정 게시글의 좋아요를 추가합니다.")
    @PostMapping("/like/{id}")
    public ApiResponse<Boolean> addLike(@PathVariable Long id) {

        boardService.addLike(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 게시글 좋아요 제거", description = "특정 게시글의 좋아요를 취소합니다.")
    @DeleteMapping("/like/{id}")
    public ApiResponse<Boolean> cancelLike(@PathVariable Long id) {

        boardService.cancelLike(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 게시글 북마크 추가", description = "특정 게시글에 북마크를 추가합니다.")
    @PostMapping("/bookmark/{id}")
    public ApiResponse<Boolean> addBookMark(@PathVariable Long id) {

        boardService.addBookmark(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 게시글 북마크 제거", description = "특정 게시글에 북마크를 취소합니다.")
    @DeleteMapping("/bookmark")
    public ApiResponse<Boolean> cancelBookMark(@PathVariable Long id) {

        boardService.cancelBookmark(id);

        return new ApiResponse<>(true);
    }
}
