package zip.ootd.ootdzip.brandrequest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.brandrequest.controller.reqeuest.BrandRequestReq;

class BrandRequestControllerTest extends ControllerTestSupport {

    @DisplayName("브랜드를 건의한다")
    @Test
    void insertBrandRequest() throws Exception {
        // given
        BrandRequestReq request = new BrandRequestReq();
        request.setRequestContents("브랜드 이름");

        // when & then
        mockMvc.perform(post("/api/v1/brand-request")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("OK"));
    }

    @DisplayName("요청 이름이 없이 브랜드를 건의하면 실패한다.")
    @Test
    void insertBrandRequestWithEmptyRequestName() throws Exception {
        // given
        BrandRequestReq request = new BrandRequestReq();
        request.setRequestContents("");

        // when & then
        mockMvc.perform(post("/api/v1/brand-request")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("requestName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("브랜드 요청 내용은 필수입니다."));
    }

}
