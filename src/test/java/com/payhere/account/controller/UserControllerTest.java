package com.payhere.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payhere.account.domain.Response.user.UserJoinResponse;
import com.payhere.account.domain.Response.user.UserLoginResponse;
import com.payhere.account.domain.dto.user.UserJoinDto;
import com.payhere.account.domain.dto.user.UserLoginDto;
import com.payhere.account.domain.entity.User;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.UserException;
import com.payhere.account.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    private String token;

    @Test
    @DisplayName("회원가입 성공 테스트")
    @WithMockUser
    void 회원가입_성공() throws Exception {

        UserJoinDto request
                = UserJoinDto
                .builder()
                .email("gunwoo@naver.com")
                .password("1234")
                .userName("김건우")
                .build();

        UserJoinResponse response = UserJoinResponse.builder()
                .userId(100L)
                .email(request.getEmail())
                .userName("김건우")
                .build();

        when(userService.join(any()))
                .thenReturn(response);

        String url = "/api/users/join";
        mockMvc.perform(post(url).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").exists())
                .andExpect(jsonPath("$.result.userId").value(100L))
                .andExpect(jsonPath("$.result.email").exists())
                .andDo(print());
        verify(userService, times(1)).join(any());

    }

    //userId가 중복 됐을 때 테스트
    @Test
    @DisplayName("회원가입 실패 : email 중복")
    @WithMockUser
    void 회원가입_실패() throws Exception {
        UserJoinDto request
                = UserJoinDto
                .builder()
                .email("gunwoo@naver.com")
                .password("1234")
                .build();

        //join을 할 시 중복네임
        when(userService.join(any()))
                .thenThrow(new UserException(ErrorCode.DUPLICATED_EMAIL,"에러 메세지"));

        mockMvc.perform(post("/api/users/join")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_EMAIL.getStatus().value()));
        verify(userService, times(1)).join(any());
    }

    //로그인 성공했을 때 테스트
    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void 로그인_성공() throws Exception {
        UserLoginDto userLoginRequest = UserLoginDto.builder()
                .email("김건우")
                .password("1234")
                .build();

        UserLoginResponse response = UserLoginResponse.builder()
                .jwt("token")
                .refreshToken("refreshToken").build();

        when(userService.login(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.jwt").exists())
                .andExpect(jsonPath("$.result.refreshToken").exists());
        verify(userService, times(1)).login(any(),any());
    }

    //userName이 존재하지 않을 때 테스트
    @Test
    @DisplayName("로그인 실패 - email 없음")
    @WithMockUser
    void 로그인실패_username_없음() throws Exception {
        UserLoginDto userLoginRequest = UserLoginDto.builder()
                .email("김건우")
                .password("1234")
                .build();

        //login 메서드를 사용하면 UserName_Not_Found 에러를 일으킬 것
        when(userService.login(any(), any()))
                .thenThrow(new UserException(ErrorCode.EMAIL_NOT_FOUND));

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.EMAIL_NOT_FOUND.getStatus().value()));
        verify(userService, times(1)).login(any(),any());
    }

    //password틀렸을 때 테스트
    @Test
    @DisplayName("로그인 실패 - password틀림")
    @WithMockUser
    void 로그인실패_password틀림() throws Exception {
        UserLoginDto userLoginRequest = UserLoginDto.builder()
                .email("김건우")
                .password("1234")
                .build();
        //login 메서드를 사용하면 INVALID_PASSWORD 에러를 일으킬 것
        when(userService.login(any(), any()))
                .thenThrow(new UserException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
        verify(userService, times(1)).login(any(),any());
    }
}