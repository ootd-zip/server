package zip.ootd.ootdzip.comment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.comment.data.CommentPostReq;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;

public class CommentControllerTest extends ControllerTestSupport {

    @DisplayName("댓글 작성")
    @Test
    void save() throws Exception {
        // given
        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(1L);
        commentPostReq.setContent("안녕하세요");
        commentPostReq.setParentDepth(0);

        Comment comment = new Comment();
        comment.setId(1L);
        when(commentService.saveComment(any(), any())).thenReturn(comment);

        // when & then
        mockMvc.perform(post("/api/v1/comment").content(objectMapper.writeValueAsString(commentPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").isNumber());
    }

    @DisplayName("댓글 작성시 ootd id 값은 필수 입니다.")
    @Test
    void saveWithoutOotdId() throws Exception {
        // given
        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setContent("안녕하세요");
        commentPostReq.setParentDepth(0);

        // when & then
        mockMvc.perform(post("/api/v1/comment").content(objectMapper.writeValueAsString(commentPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("댓글 작성시 부모댓글인지 자식댓글인지 알려주어야 합니다.")
    @Test
    void saveWithoutParentDepth() throws Exception {
        // given
        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(1L);
        commentPostReq.setContent("안녕하세요");

        // when & then
        mockMvc.perform(post("/api/v1/comment").content(objectMapper.writeValueAsString(commentPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("댓글 작성시 부모댓글인지 자식댓글인지 값에 대해 0 이상 입니다.")
    @Test
    void saveUnderflowParentDepth() throws Exception {
        // given
        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(1L);
        commentPostReq.setContent("안녕하세요");
        commentPostReq.setParentDepth(-1);

        // when & then
        mockMvc.perform(post("/api/v1/comment").content(objectMapper.writeValueAsString(commentPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("댓글 작성시 부모댓글인지 자식댓글인지 값에 대해 1 이하 입니다.")
    @Test
    void saveOverflowParentDepth() throws Exception {
        // given
        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(1L);
        commentPostReq.setContent("안녕하세요");
        commentPostReq.setParentDepth(2);

        // when & then
        mockMvc.perform(post("/api/v1/comment").content(objectMapper.writeValueAsString(commentPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("댓글 작성시 댓글내용이 있어야 합니다.")
    @Test
    void saveWithoutContent() throws Exception {
        // given
        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(1L);
        commentPostReq.setContent("안녕하세요");
        commentPostReq.setParentDepth(2);

        // when & then
        mockMvc.perform(post("/api/v1/comment").content(objectMapper.writeValueAsString(commentPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("댓글 작성시 댓글내용은 3000자 이하 합니다.")
    @Test
    void saveWithTooLongContent() throws Exception {
        // given
        CommentPostReq commentPostReq = new CommentPostReq();
        commentPostReq.setOotdId(1L);

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            content.append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다");
        }
        content.append("메");
        commentPostReq.setContent(content.toString());
        commentPostReq.setParentDepth(2);

        // when & then
        mockMvc.perform(post("/api/v1/comment").content(objectMapper.writeValueAsString(commentPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("댓글 삭제")
    @Test
    void deleteComment() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/comment/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("댓글 전체 조회")
    @Test
    void getComments() throws Exception {
        // given
        Long ootdId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(commentService.getComments(any(), any()))
                .thenReturn(new CommonSliceResponse<>(List.of(), pageable, false));

        // when & then
        mockMvc.perform(get("/api/v1/comments")
                        .param("ootdId", ootdId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("댓글 전체 조회시 Ootd id 는 필수입니다.")
    @Test
    void getCommentsNoOotdId() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        when(commentService.getComments(any(), any()))
                .thenReturn(new CommonSliceResponse<>(List.of(), pageable, false));

        // when & then
        mockMvc.perform(get("/api/v1/comments"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }
}
