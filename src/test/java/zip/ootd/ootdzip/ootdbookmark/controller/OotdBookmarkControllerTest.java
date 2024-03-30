package zip.ootd.ootdzip.ootdbookmark.controller;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.common.response.CommonPageResponse;

public class OotdBookmarkControllerTest extends ControllerTestSupport {

    @DisplayName("해당 유저 북마크 전체 조회")
    @Test
    void getBookmarks() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        when(ootdBookmarkService.getOotdBookmarks(any(), any()))
                .thenReturn(new CommonPageResponse<>(List.of(), pageable, false, 100L));

        // when & then
        mockMvc.perform(get("/api/v1/bookmarks")
                        .param("size", "10")
                        .param("page", "0")
                        .param("sortCriteria", "createdAt")
                        .param("sortDirection", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("북마크 제거")
    @Test
    void deleteBookmarks() throws Exception {
        // given

        // when & then
        mockMvc.perform(delete("/api/v1/bookmarks")
                        .param("ootdBookmarkIds", "1", "2", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("북마크 제거시에 제거할 ootd id 는 필수입니다.")
    @Test
    void deleteBookmarksNoOotdId() throws Exception {
        // given

        // when & then
        mockMvc.perform(delete("/api/v1/bookmarks"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }
}
