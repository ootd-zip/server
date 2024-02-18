package zip.ootd.ootdzip.clothes.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.StringContains.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.clothes.controller.request.SaveClothesReq;
import zip.ootd.ootdzip.clothes.controller.request.UpdateClothesReq;
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;

class ClothesControllerTest extends ControllerTestSupport {

    @DisplayName("옷을 저장한다.")
    @Test
    void saveClothes() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
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
                .purchaseStoreType(PurchaseStoreType.Write)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("purchaseStore"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("구매처는 필수입니다."));
    }

    @DisplayName("옷을 저장할 때 브랜드 ID는 양수이다.")
    @Test
    void saveClothesWithZeroBrandId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(0L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("brandId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("브랜드 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 저장할 때 카테고리 ID는 양수이다.")
    @Test
    void saveClothesWithZeroCategoryId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(0L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("categoryId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("카테고리 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 저장할 때 색은 필수이다.")
    @Test
    void saveClothesWithBlankColorIds() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("colorIds"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("색은 필수입니다."));
    }

    @DisplayName("옷을 저장할 때 색 ID는 양수여야 한다.")
    @Test
    void saveClothesWithZeroColorId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(0L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("colorIds[0]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("색 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 저장할 때 공개여부는 필수이다.")
    @Test
    void saveClothesWithoutIsOpen() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("isOpen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("공개여부는 필수입니다."));
    }

    @DisplayName("옷을 저장할 때 사이즈 ID는 양수여야 한다.")
    @Test
    void saveClothesWithZeroSizeId() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(0L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("sizeId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("사이즈 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 저장할 때 이미지는 필수이다.")
    @Test
    void saveClothesWithoutClothesImageUrl() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("clothesImageUrl"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("이미지는 필수입니다."));
    }

    @DisplayName("옷을 저장할 때 제품명은 필수이다.")
    @Test
    void saveClothesWithoutName() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("제품명은 필수입니다."));
    }

    @DisplayName("옷을 저장할 때 메모는 최대 2000자이다.")
    @Test
    void saveClothesWithTooLongMemo() throws Exception {
        // given
        StringBuilder tooLongMemo = new StringBuilder();
        // 3000자
        for (int i = 0; i < 20; i++) {
            tooLongMemo.append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다");
        }
        tooLongMemo.append("메모");

        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명")
                .memo(tooLongMemo.toString())
                .purchaseDate("구매시기1")
                .build();

        // when & then
        assertThat(tooLongMemo.toString().length()).isGreaterThan(2000);

        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("memo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("메모는 최대 2000자입니다."));
    }

    @DisplayName("옷을 저장할 때 구매처 타입은 필수이다.")
    @Test
    void saveClothesWithoutPurchaseStoreType() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명")
                .memo("메모")
                .purchaseDate("구매시기1")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/clothes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("purchaseStoreType"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("유효하지 않은 구매처 타입입니다."));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").value(containsString("옷 ID는 양수여야 합니다.")));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("userId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("유저 ID는 양수여야 합니다."));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").value(containsString("옷 ID는 양수여야 합니다.")));
    }

    @DisplayName("옷 세부정보를 수정한다")
    @Test
    void updateClothes() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        when(clothesService.updateClothes(any(), any())).thenReturn(new SaveClothesRes(clothesId));

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").isNumber());
    }

    @DisplayName("옷을 수정할 때 옷 ID는 양수여야한다.")
    @Test
    void updateClothesWithZeroClothesId() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 0L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").value(containsString("옷 ID는 양수여야 합니다.")));
    }

    @DisplayName("옷을 수정할 때 구매처는 필수이다.")
    @Test
    void updateClothesWithoutPurchaseStore() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("purchaseStore"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("구매처는 필수입니다."));
    }

    @DisplayName("옷을 수정할 때 브랜드 ID는 양수이다.")
    @Test
    void updateClothesWithZeroBrandId() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(0L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("brandId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("브랜드 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 수정할 때 카테고리 ID는 양수이다.")
    @Test
    void updateClothesWithZeroCategoryId() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(0L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("categoryId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("카테고리 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 수정할 때 색은 필수이다.")
    @Test
    void updateClothesWithBlankColorIds() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(new ArrayList<>())
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("colorIds"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("색은 필수입니다."));
    }

    @DisplayName("옷을 수정할 때 색 ID는 양수여야 한다.")
    @Test
    void updateClothesWithZeroColorId() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(0L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("colorIds[0]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("색 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 수정할 때 공개여부는 필수이다.")
    @Test
    void updateClothesWithoutIsOpen() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("isOpen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("공개여부는 필수입니다."));
    }

    @DisplayName("옷을 수정할 때 사이즈 ID는 양수여야 한다.")
    @Test
    void updateClothesWithZeroSizeId() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(0L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("sizeId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("사이즈 ID는 양수여야 합니다."));
    }

    @DisplayName("옷을 수정할 때 이미지는 필수이다.")
    @Test
    void updateClothesWithoutClothesImageUrl() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("clothesImageUrl"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("이미지는 필수입니다."));
    }

    @DisplayName("옷을 수정할 때 제품명은 필수이다.")
    @Test
    void updateClothesWithoutName() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("제품명은 필수입니다."));
    }

    @DisplayName("옷을 수정할 때 메모는 최대 2000자이다.")
    @Test
    void updateClothesWithTooLongMemo() throws Exception {
        // given
        StringBuilder tooLongMemo = new StringBuilder();
        // 3000자
        for (int i = 0; i < 20; i++) {
            tooLongMemo.append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다");
        }
        tooLongMemo.append("메모");

        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .purchaseStoreType(PurchaseStoreType.Write)
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo(tooLongMemo.toString())
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        assertThat(tooLongMemo.toString().length()).isGreaterThan(2000);

        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("memo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("메모는 최대 2000자입니다."));
    }

    @DisplayName("옷을 수정할 때 구매처 타입은 필수이다.")
    @Test
    void updateClothesWithoutPurchaseStoreType() throws Exception {
        // given
        UpdateClothesReq request = UpdateClothesReq.builder()
                .purchaseStore("구매처1")
                .brandId(1L)
                .categoryId(1L)
                .colorIds(List.of(1L))
                .isOpen(true)
                .sizeId(1L)
                .clothesImageUrl("image1.jpg")
                .name("제품명1")
                .memo("메모입니다.")
                .purchaseDate("구매시기1")
                .build();

        Long clothesId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/clothes/{id}", clothesId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("purchaseStoreType"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("유효하지 않은 구매처 타입입니다."));
    }
}
