package zip.ootd.ootdzip.report.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.report.controller.request.ReportReq;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.service.request.ReportType;

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

    @DisplayName("신고한다.")
    @Test
    void report() throws Exception {
        // given
        ReportReq request = new ReportReq(1L, 1L, ReportType.OOTD);

        when(reportService.report(any(), any())).thenReturn(ReportResultRes.of(1L, 1));

        // when & then
        mockMvc.perform(post("/api/v1/report")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("신고할 때 신고 ID는 양수이다.")
    @Test
    void reportWithZeroReportId() throws Exception {
        // given
        ReportReq request = new ReportReq(0L, 1L, ReportType.OOTD);

        // when & then
        mockMvc.perform(post("/api/v1/report")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("신고할 때 target ID는 양수이다.")
    @Test
    void reportWithZeroTargetId() throws Exception {
        // given
        ReportReq request = new ReportReq(1L, 0L, ReportType.OOTD);

        // when & then
        mockMvc.perform(post("/api/v1/report")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("신고할 때 신고유형은 필수이다.")
    @Test
    void reportWithoutReportType() throws Exception {
        // given
        ReportReq request = new ReportReq(1L, 1L, null);

        // when & then
        mockMvc.perform(post("/api/v1/report")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

}
