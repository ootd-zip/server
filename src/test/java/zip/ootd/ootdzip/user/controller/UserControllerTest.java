package zip.ootd.ootdzip.user.controller;

import static org.hamcrest.core.StringContains.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.user.controller.response.UserInfoForMyPageRes;

class UserControllerTest extends ControllerTestSupport {

    @DisplayName("userId로 마이페이지 정보를 조회한다.")
    @Test
    void getUserInfoForMyPage() throws Exception {
        // given
        Long id = 1L;

        when(userService.getUserInfoForMyPage(any(), any())).thenReturn(new UserInfoForMyPageRes());

        // when & then
        mockMvc.perform(get("/api/v1/user/{id}/mypage", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("마이페이지 정보를 조회할 때 userId는 양수여야한다.")
    @Test
    void getUserInfoForMyPageWithZeroId() throws Exception {
        // given
        Long id = 0L;

        when(userService.getUserInfoForMyPage(any(), any())).thenReturn(new UserInfoForMyPageRes());

        // when & then
        mockMvc.perform(get("/api/v1/user/{id}/mypage", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").value(containsString("유저 ID는 양수여야 합니다.")));
    }
}
