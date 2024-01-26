package zip.ootd.ootdzip.clothes.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.clothes.controller.request.SaveClothesReq;
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;

class ClothesControllerTest extends ControllerTestSupport {

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @DisplayName("옷을 저장한다.")
    @Test
    void saveClothes() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        when(clothesService.saveClothes(any(), any())).thenReturn(new SaveClothesRes(1L));

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").isNumber());
    }

    @DisplayName("옷을 저장할 때 구매처는 필수이다.")
    @Test
    void saveClothesWithoutPurchaseStore() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 브랜드 ID는 양수이다.")
    @Test
    void saveClothesWithZeroBrandId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(0L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 카테고리 ID는 양수이다.")
    @Test
    void saveClothesWithZeroCategoryId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(0L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 색은 필수이다.")
    @Test
    void saveClothesWithBlankColorIds() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 색 ID는 양수여야 한다.")
    @Test
    void saveClothesWithZeroColorId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(0L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 공개여부는 필수이다.")
    @Test
    void saveClothesWithoutIsOpen() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 사이즈 ID는 양수여야 한다.")
    @Test
    void saveClothesWithZeroSizeId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(0L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 이미지는 필수이다.")
    @Test
    void saveClothesWithoutClothesImageUrl() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .name("제품명1")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷을 저장할 때 제품명은 필수이다.")
    @Test
    void saveClothesWithoutName() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(0L)
                .clothesImageUrl("image1.jpg")
                .material("재질1")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷 ID로 옷을 조회한다.")
    @Test
    void findClothesById() throws Exception {
        // given
        Long id = 1L;

        when(clothesService.findClothesById(anyLong(), any())).thenReturn(new FindClothesRes());

        // when & then
        mockMvc.perform(get("/api/v1/clothes/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("옷 ID로 옷을 조회할 때 옷 ID는 양수여야 한다.")
    @Test
    void findClothesByIdWithZeroId() throws Exception {
        // given
        Long id = 0L;

        when(clothesService.findClothesById(anyLong(), any())).thenReturn(new FindClothesRes());

        // when & then
        mockMvc.perform(get("/api/v1/clothes/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").exists());
    }

    @DisplayName("유저 ID로 옷을 조회한다.")
    @Test
    void findClothesByUser() throws Exception {
        // given
        when(clothesService.findClothesByUser(any(), any())).thenReturn(List.of());

        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>();
        requestParam.set("userId", "1");

        // when & then
        mockMvc.perform(get("/api/v1/clothes").params(requestParam))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isArray());
    }

    @DisplayName("유저 ID로 옷을 조회할 때 유저 ID는 양수여야 한다.")
    @Test
    void findClothesByUserWithZeroUserId() throws Exception {
        // given
        when(clothesService.findClothesByUser(any(), any())).thenReturn(List.of());

        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>();
        requestParam.set("userId", "0");

        // when & then
        mockMvc.perform(get("/api/v1/clothes").params(requestParam))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray());
    }

    @DisplayName("옷 ID로 옷을 삭제한다.")
    @Test
    void deleteClothesById() throws Exception {
        // given
        Long clothesId = 1L;

        when(clothesService.deleteClothesById(anyLong(), any())).thenReturn(new DeleteClothesByIdRes());

        // when & then
        mockMvc.perform(delete("/api/v1/clothes/{id}", clothesId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").exists());
    }

    @DisplayName("옷 ID로 옷을 삭제할 때 옷 ID는 양수여야 한다.")
    @Test
    void deleteClothesByIdWithZeroUserId() throws Exception {
        // given
        Long clothesId = 0L;

        when(clothesService.deleteClothesById(anyLong(), any())).thenReturn(new DeleteClothesByIdRes());

        // when & then
        mockMvc.perform(delete("/api/v1/clothes/{id}", clothesId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").exists());
    }
}
