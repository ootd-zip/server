package zip.ootd.ootdzip.notification.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;

public class NotificationControllerTest extends ControllerTestSupport {

    @DisplayName("알람 전체 조회")
    @ParameterizedTest
    @CsvSource({
            "false",
            "true"
    })
    void getComments(Boolean isRead) throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        when(notificationService.getNotifications(any(), any()))
                .thenReturn(new CommonSliceResponse<>(List.of(), pageable, false));

        // when & then
        mockMvc.perform(get("/api/v1/notification")
                        .param("isRead", String.valueOf(isRead)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("알림조회시 읽음, 읽지않음을 보내지 않으면 실패 합니다.")
    @Test
    void getCommentsWithoutIsRead() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        when(notificationService.getNotifications(any(), any()))
                .thenReturn(new CommonSliceResponse<>(List.of(), pageable, false));

        // when & then
        mockMvc.perform(get("/api/v1/notification"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("알람 읽음 수정")
    @Test
    void updateIsRead() throws Exception {
        // given

        // when & then
        mockMvc.perform(patch("/api/v1/notification/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }
}
