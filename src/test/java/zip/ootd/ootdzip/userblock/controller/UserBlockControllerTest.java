package zip.ootd.ootdzip.userblock.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;

class UserBlockControllerTest extends ControllerTestSupport {

    @DisplayName("사용자를 차단한다.")
    @Test
    void blockUser() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/user-block/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200));
    }

    @DisplayName("사용자를 차단할 때 userId가 양수여야 한다.")
    @Test
    void blockUserWithZeroUserId() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/user-block/0"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404));
    }
}