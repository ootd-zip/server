package zip.ootd.ootdzip.ootd.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.ootd.data.OotdGetRes;
import zip.ootd.ootdzip.ootd.data.OotdPatchReq;
import zip.ootd.ootdzip.ootd.data.OotdPostReq;
import zip.ootd.ootdzip.ootd.data.OotdPutReq;
import zip.ootd.ootdzip.ootd.domain.Ootd;

public class OotdControllerTest extends ControllerTestSupport {

    @DisplayName("OOTD 게시글 저장")
    @Test
    void save() throws Exception {
        // given
        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(1L);
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(2L);
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPostReq.OotdImageReq ootdImageReq = new OotdPostReq.OotdImageReq();
        ootdImageReq.setOotdImage("input_image_url");
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        OotdPostReq ootdPostReq = new OotdPostReq();
        ootdPostReq.setIsPrivate(false);
        ootdPostReq.setContent("테스트");
        ootdPostReq.setStyles(Arrays.asList(1L, 2L));
        ootdPostReq.setOotdImages(List.of(ootdImageReq));

        Ootd ootd = new Ootd();
        ootd.setId(1L);
        when(ootdService.postOotd(any(), any())).thenReturn(ootd);

        // when & then
        mockMvc.perform(post("/api/v1/ootd").content(objectMapper.writeValueAsString(ootdPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").isNumber());
    }

    @DisplayName("OOTD 게시글 저장할때는 Ootd 이미지가 최소 1장 이상이어야 한다.")
    @Test
    void saveWithoutOotdImages() throws Exception {
        // given
        OotdPostReq ootdPostReq = new OotdPostReq();
        ootdPostReq.setIsPrivate(false);
        ootdPostReq.setContent("테스트");
        ootdPostReq.setStyles(Arrays.asList(1L, 2L));

        // when & then
        mockMvc.perform(post("/api/v1/ootd").content(objectMapper.writeValueAsString(ootdPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 이미지 링크는 필수입니다.")
    @Test
    void saveWithoutOotdImage() throws Exception {
        // given
        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(1L);
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(2L);
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPostReq.OotdImageReq ootdImageReq = new OotdPostReq.OotdImageReq();
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        OotdPostReq ootdPostReq = new OotdPostReq();
        ootdPostReq.setIsPrivate(false);
        ootdPostReq.setContent("테스트");
        ootdPostReq.setStyles(Arrays.asList(1L, 2L));
        ootdPostReq.setOotdImages(List.of(ootdImageReq));

        // when & then
        mockMvc.perform(post("/api/v1/ootd").content(objectMapper.writeValueAsString(ootdPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 게시글은 최대 3000자 입니다.")
    @Test
    void saveWithTooLongContent() throws Exception {
        // given
        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(1L);
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPostReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPostReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(2L);
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPostReq.OotdImageReq ootdImageReq = new OotdPostReq.OotdImageReq();
        ootdImageReq.setOotdImage("input_image_url");
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            content.append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다");
        }
        content.append("메");

        OotdPostReq ootdPostReq = new OotdPostReq();
        ootdPostReq.setIsPrivate(false);
        ootdPostReq.setContent(content.toString());
        ootdPostReq.setStyles(Arrays.asList(1L, 2L));
        ootdPostReq.setOotdImages(List.of(ootdImageReq));

        // when & then
        mockMvc.perform(post("/api/v1/ootd").content(objectMapper.writeValueAsString(ootdPostReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 공개여부 수정")
    @Test
    void updateContentAndIsPrivate() throws Exception {
        // given
        OotdPatchReq ootdPatchReq = new OotdPatchReq();
        ootdPatchReq.setIsPrivate(true);

        // when & then
        mockMvc.perform(patch("/api/v1/ootd/{id}", 1L)
                        .content(objectMapper.writeValueAsString(ootdPatchReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("OOTD 공개여부 수정시 공개여부는 필수입니다.")
    @Test
    void updateContentAndIsPrivateWithoutIsPrivate() throws Exception {
        // given
        OotdPatchReq ootdPatchReq = new OotdPatchReq();

        // when & then
        mockMvc.perform(patch("/api/v1/ootd/{id}", 1L)
                        .content(objectMapper.writeValueAsString(ootdPatchReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 게시글 전체수정")
    @Test
    void updateAll() throws Exception {
        // given
        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(1L);
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(2L);
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPutReq.OotdImageReq ootdImageReq = new OotdPutReq.OotdImageReq();
        ootdImageReq.setOotdImage("input_image_url");
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        OotdPutReq ootdPutReq = new OotdPutReq();
        ootdPutReq.setIsPrivate(false);
        ootdPutReq.setContent("테스트");
        ootdPutReq.setStyles(Arrays.asList(1L, 2L));
        ootdPutReq.setOotdImages(List.of(ootdImageReq));

        // when & then
        mockMvc.perform(put("/api/v1/ootd/{id}", 1L)
                        .content(objectMapper.writeValueAsString(ootdPutReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("OOTD 게시글 전체수정시 공개여부는 필수입니다.")
    @Test
    void updateAllWithoutIsPrivate() throws Exception {
        // given
        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(1L);
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(2L);
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPutReq.OotdImageReq ootdImageReq = new OotdPutReq.OotdImageReq();
        ootdImageReq.setOotdImage("input_image_url");
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        OotdPutReq ootdPutReq = new OotdPutReq();
        ootdPutReq.setContent("테스트");
        ootdPutReq.setStyles(Arrays.asList(1L, 2L));
        ootdPutReq.setOotdImages(List.of(ootdImageReq));

        // when & then
        mockMvc.perform(put("/api/v1/ootd/{id}", 1L)
                        .content(objectMapper.writeValueAsString(ootdPutReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 게시글 전체수정시 OOTD 이미지는 최소 1장 입니다.")
    @Test
    void updateAllWithoutOotdImages() throws Exception {
        // given
        OotdPutReq ootdPutReq = new OotdPutReq();
        ootdPutReq.setIsPrivate(false);
        ootdPutReq.setContent("테스트");
        ootdPutReq.setStyles(Arrays.asList(1L, 2L));

        // when & then
        mockMvc.perform(put("/api/v1/ootd/{id}", 1L)
                        .content(objectMapper.writeValueAsString(ootdPutReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 게시글 전체수정시 OOTD 이미지 URL 은 필수 입니다.")
    @Test
    void updateAllWithoutOotdImage() throws Exception {
        // given
        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(1L);
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(2L);
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPutReq.OotdImageReq ootdImageReq = new OotdPutReq.OotdImageReq();
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        OotdPutReq ootdPutReq = new OotdPutReq();
        ootdPutReq.setIsPrivate(false);
        ootdPutReq.setContent("테스트");
        ootdPutReq.setStyles(Arrays.asList(1L, 2L));
        ootdPutReq.setOotdImages(List.of(ootdImageReq));

        // when & then
        mockMvc.perform(put("/api/v1/ootd/{id}", 1L)
                        .content(objectMapper.writeValueAsString(ootdPutReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 게시글 전체수정시 게시글은 3000자 이하 입니다.")
    @Test
    void updateAllWithTooLongContent() throws Exception {
        // given
        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq.setClothesId(1L);
        clothesTagReq.setDeviceWidth(100L);
        clothesTagReq.setDeviceHeight(50L);
        clothesTagReq.setXRate("22.33");
        clothesTagReq.setYRate("33.44");

        OotdPutReq.OotdImageReq.ClothesTagReq clothesTagReq1 = new OotdPutReq.OotdImageReq.ClothesTagReq();
        clothesTagReq1.setClothesId(2L);
        clothesTagReq1.setDeviceWidth(100L);
        clothesTagReq1.setDeviceHeight(50L);
        clothesTagReq1.setXRate("33.44");
        clothesTagReq1.setYRate("44.55");

        OotdPutReq.OotdImageReq ootdImageReq = new OotdPutReq.OotdImageReq();
        ootdImageReq.setOotdImage("input_image_url");
        ootdImageReq.setClothesTags(Arrays.asList(clothesTagReq, clothesTagReq1));

        OotdPutReq ootdPutReq = new OotdPutReq();
        ootdPutReq.setIsPrivate(false);

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            content.append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다");
        }
        content.append("메");
        ootdPutReq.setContent(content.toString());
        ootdPutReq.setStyles(Arrays.asList(1L, 2L));
        ootdPutReq.setOotdImages(List.of(ootdImageReq));

        // when & then
        mockMvc.perform(put("/api/v1/ootd/{id}", 1L)
                        .content(objectMapper.writeValueAsString(ootdPutReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("OOTD 게시글 좋아요 추가")
    @Test
    void addLike() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(post("/api/v1/ootd/like/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("OOTD 게시글 좋아요 취소")
    @Test
    void cancelLike() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/ootd/like/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("OOTD 게시글 북마크 추가")
    @Test
    void addBookmark() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(post("/api/v1/ootd/bookmark/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("OOTD 게시글 북마크 취소")
    @Test
    void cancelBookmark() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/ootd/bookmark/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }

    @DisplayName("OOTD 게시글 조회")
    @Test
    void getOotd() throws Exception {
        // given
        Long id = 1L;

        when(ootdService.getOotd(any(), any())).thenReturn(new OotdGetRes());

        // when & then
        mockMvc.perform(get("/api/v1/ootd/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("다른 OOTD 게시글 조회")
    @Test
    void getOotdOther() throws Exception {
        // given
        Long userId = 1L;
        Long ootdId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(ootdService.getOotdOther(any(), any()))
                .thenReturn(new CommonSliceResponse<>(List.of(), pageable, true));

        // when & then
        mockMvc.perform(get("/api/v1/ootd/other")
                        .param("userId", Long.toString(userId))
                        .param("ootdId", ootdId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("비슷한 OOTD 게시글 조회")
    @Test
    void getOotdSimilar() throws Exception {
        // given
        Long ootdId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(ootdService.getOotdSimilar(any(), any()))
                .thenReturn(new CommonSliceResponse<>(List.of(), pageable, false));

        // when & then
        mockMvc.perform(get("/api/v1/ootd/similar")
                        .param("ootdId", ootdId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("선택한 유저 OOTD 게시글 조회")
    @Test
    void getUserOotd() throws Exception {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(ootdService.getOotdByUser(any(), any()))
                .thenReturn(new CommonSliceResponse<>(List.of(), pageable, true));

        // when & then
        mockMvc.perform(get("/api/v1/ootd")
                        .param("userId", Long.toString(userId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("비슷한 OOTD 게시글 조회")
    @Test
    void getOotdByClothes() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        when(ootdService.getOotdByClothes(any(), any()))
                .thenReturn(new CommonPageResponse<>(List.of(), pageable, false, 100L));

        // when & then
        mockMvc.perform(get("/api/v1/ootd/clothes")
                        .param("clothesId", "1")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }
}
