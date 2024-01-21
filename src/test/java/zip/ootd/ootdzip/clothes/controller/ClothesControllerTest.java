package zip.ootd.ootdzip.clothes.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import zip.ootd.ootdzip.ControllerTestSupport;
import zip.ootd.ootdzip.clothes.controller.request.SaveClothesReq;
import zip.ootd.ootdzip.user.domain.User;

class ClothesControllerTest extends ControllerTestSupport {

    @DisplayName("옷을 저장한다.")
    @Test
    void saveClothes() throws Exception {
        // given
        SaveClothesReq request = SaveClothesReq
                .builder()
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

        when(userService.getAuthenticatiedUser()).thenReturn(User.getDefault());

        // when & then
        mockMvc.perform(
                        post("/api/v1/clothes")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}