package zip.ootd.ootdzip.report.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;

class ReportControllerTest extends ControllerTestSupport {

    @DisplayName("신고 항목을 조회한다.")
    @Test
    void findClothesByUser() throws Exception {
        // given
        when(reportService.getAllReports()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/report"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isArray());
    }
}
