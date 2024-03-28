package zip.ootd.ootdzip.user.controller;

import static org.hamcrest.core.StringContains.*;
import static org.mockito.ArgumentMatchers.*;
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
import zip.ootd.ootdzip.user.controller.request.ProfileReq;
import zip.ootd.ootdzip.user.controller.request.UserRegisterReq;
import zip.ootd.ootdzip.user.controller.request.UserStyleUpdateReq;
import zip.ootd.ootdzip.user.controller.response.UserInfoForMyPageRes;
import zip.ootd.ootdzip.user.data.FollowReq;
import zip.ootd.ootdzip.user.domain.UserGender;

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

    @DisplayName("유저 정보를 등록한다.")
    @Test
    void register() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(UserGender.FEMALE)
                .age(100)
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .styles(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("회원가입 성공"));
    }

    @DisplayName("유저 정보를 등록할 때 닉네임은 필수이다.")
    @Test
    void registerWithoutName() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("")
                .gender(UserGender.FEMALE)
                .age(100)
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .styles(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("닉네임을 입력해주세요."));
    }

    @DisplayName("유저 정보를 등록할 때 성별은 필수이다.")
    @Test
    void registerWithoutGender() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(null)
                .age(100)
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .styles(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("gender"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("성별이 유효하지 않습니다."));
    }

    @DisplayName("유저 정보를 등록할 때 나이는 양수여야한다.")
    @Test
    void registerWithZeroAge() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(UserGender.FEMALE)
                .age(0)
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .styles(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("age"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("나이는 양수여야 합니다."));
    }

    @DisplayName("유저 정보를 등록할 때 키는 양수여야 합니다.")
    @Test
    void registerWithZeroHeight() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(UserGender.FEMALE)
                .age(100)
                .height(0)
                .weight(80)
                .isBodyPrivate(false)
                .styles(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("height"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("키는 양수여야 합니다."));
    }

    @DisplayName("유저 정보를 등록할 때 몸무게는 양수여야 합니다.")
    @Test
    void registerWithZeroWeight() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(UserGender.FEMALE)
                .age(100)
                .height(180)
                .weight(0)
                .isBodyPrivate(false)
                .styles(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("weight"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("몸무게는 양수여야 합니다."));
    }

    @DisplayName("유저 정보를 등록할 때 체형정보 공개여부는 필수입니다.")
    @Test
    void registerWithoutisBodyPrivate() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(UserGender.FEMALE)
                .age(100)
                .height(180)
                .weight(80)
                .isBodyPrivate(null)
                .styles(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("isBodyPrivate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("체형정보 공개여부는 필수입니다."));
    }

    @DisplayName("유저 정보를 등록할 때 스타일은 3개 이상 입력해야 합니다.")
    @Test
    void registerWithoutStyle() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(UserGender.FEMALE)
                .age(100)
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .styles(List.of(1L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("styles"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("스타일은 3개 이상 입력해주세요."));
    }

    @DisplayName("유저 정보를 등록할 때 스타일 id는 양수여야 합니다.")
    @Test
    void registerWithZeroStyleId() throws Exception {
        // given
        UserRegisterReq request = UserRegisterReq.builder()
                .name("유저1")
                .gender(UserGender.FEMALE)
                .age(100)
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .styles(List.of(0L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/user/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("styles[0]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("스타일 id는 양수여야 합니다."));
    }

    @DisplayName("로그인한 유저 프로필을 업데이트한다.")
    @Test
    void updateProfile() throws Exception {
        // given
        ProfileReq request = ProfileReq.builder()
                .name("유저1")
                .profileImage("image.jpg")
                .description("소개")
                .height(180)
                .weight(70)
                .isBodyPrivate(true)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/user/profile").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("프로필 정보 업데이트 성공"));
    }

    @DisplayName("프로필을 업데이트할 때 닉네임은 필수입니다.")
    @Test
    void updateProfileWithoutName() throws Exception {
        // given
        ProfileReq request = ProfileReq.builder()
                .name(null)
                .profileImage("image.jpg")
                .description("소개")
                .height(180)
                .weight(70)
                .isBodyPrivate(true)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/user/profile").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("닉네임은 필수입니다."));
    }

    @DisplayName("프로필을 업데이트할 때 소개는 최대 2000자 입니다.")
    @Test
    void updateProfileWithLongDescription() throws Exception {
        // given
        StringBuilder tooLongDescription = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            tooLongDescription.append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다")
                    .append("메모입니다메모입니다메모입니다메모입니다");
        }
        tooLongDescription.append("메모");

        ProfileReq request = ProfileReq.builder()
                .name("유저1")
                .profileImage("image.jpg")
                .description(tooLongDescription.toString())
                .height(180)
                .weight(70)
                .isBodyPrivate(true)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/user/profile").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("소개는 최대 2000자 입니다."));
    }

    @DisplayName("프로필을 업데이트할 때 키는 양수여야 합니다.")
    @Test
    void updateProfileWithZeroHeight() throws Exception {
        // given
        ProfileReq request = ProfileReq.builder()
                .name("유저1")
                .profileImage("image.jpg")
                .description("소개")
                .height(0)
                .weight(70)
                .isBodyPrivate(true)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/user/profile").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("height"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("키는 양수여야 합니다."));
    }

    @DisplayName("프로필을 업데이트할 때 몸무게는 양수여야 합니다.")
    @Test
    void updateProfileWithZeroWeight() throws Exception {
        // given
        ProfileReq request = ProfileReq.builder()
                .name("유저1")
                .profileImage("image.jpg")
                .description("소개")
                .height(180)
                .weight(0)
                .isBodyPrivate(true)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/user/profile").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("weight"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("몸무게는 양수여야 합니다."));
    }

    @DisplayName("프로필을 업데이트할 때 체형정보 공개여부는 필수입니다.")
    @Test
    void updateProfileWithoutIsBodyPrivate() throws Exception {
        // given
        ProfileReq request = ProfileReq.builder()
                .name("유저1")
                .profileImage("image.jpg")
                .description("소개")
                .height(180)
                .weight(70)
                .isBodyPrivate(null)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/user/profile").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("isBodyPrivate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("체형정보 공개여부는 필수입니다."));
    }

    @DisplayName("유저 선호 스타일을 업데이트한다.")
    @Test
    void updateUserStyle() throws Exception {
        // given
        UserStyleUpdateReq request = UserStyleUpdateReq.builder()
                .styleIds(List.of(1L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(put("/api/v1/user/user-styles")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("ok"));
    }

    @DisplayName("유저 선호 스타일을 업데이트할 때 스타일 ID는 양수여야 한다.")
    @Test
    void updateUserStyleWithZeroStyleId() throws Exception {
        // given
        UserStyleUpdateReq request = UserStyleUpdateReq.builder()
                .styleIds(List.of(0L, 2L, 3L))
                .build();

        // when & then
        mockMvc.perform(put("/api/v1/user/user-styles")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("styleIds[0]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("스타일 ID는 양수여야 합니다."));
    }

    @DisplayName("유저 선호 스타일을 업데이트할 때 스타일 ID는 최소 3개가 필요하다.")
    @Test
    void updateUserStyleWith2Styles() throws Exception {
        // given
        UserStyleUpdateReq request = UserStyleUpdateReq.builder()
                .styleIds(List.of(1L, 2L))
                .build();

        // when & then
        mockMvc.perform(put("/api/v1/user/user-styles")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("styleIds"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].reason").value("스타일은 3개 이상 입력해주세요."));
    }

    @DisplayName("언팔로워가 성공한다.")
    @Test
    void unfollower() throws Exception {
        // given
        FollowReq request = new FollowReq();
        request.setUserId(1L);
        when(userService.removeFollower(any(), any())).thenReturn(true);

        // when & then
        mockMvc.perform(delete("/api/v1/user/follower").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isBoolean());
    }
}
