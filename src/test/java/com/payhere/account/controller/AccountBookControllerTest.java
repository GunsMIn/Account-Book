package com.payhere.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payhere.account.config.encrypt.EncrypterConfig;
import com.payhere.account.domain.Response.accountBook.AccountAddResponse;
import com.payhere.account.domain.Response.accountBook.AccountBookDeleteResponse;
import com.payhere.account.domain.Response.accountBook.AccountModifyResponse;
import com.payhere.account.domain.Response.accountBook.AccountSelectResponse;
import com.payhere.account.domain.dto.accountBook.AccountAddDto;
import com.payhere.account.domain.dto.accountBook.AccountModifyDto;
import com.payhere.account.exception.customException.AccountException;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.UserException;
import com.payhere.account.service.AccountBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountBookController.class)
class AccountBookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountBookService accountBookService;

    @MockBean
    EncrypterConfig encoderConfig;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("조회")
    class select {

        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 단건 조회 성공")
        void 가계부_단건_조회_성공() throws Exception {
            Long id = 1L;
            //조회 시 응답
            AccountSelectResponse response = AccountSelectResponse.builder()
                    .id(id)
                    .balance(100000)
                    .title("테스트 제목")
                    .memo("테스트 메모")
                    .userName("김건우")
                    .email("gunwoo@naver.com")
                    .build();

            //Service의 조회 메서드 사용시 post entity 반환
            when(accountBookService.getBook(any(),any()))
                    .thenReturn(response);

            String url = "/api/account_books/" +id;
            mockMvc.perform(get(url)
                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.title").exists())
                    .andExpect(jsonPath("$.result.balance").value(100000))
                    .andExpect(jsonPath("$.result.title").value("테스트 제목"))
                    .andExpect(jsonPath("$.result.memo").value("테스트 메모"))
                    .andExpect(jsonPath("$.result.userName").value("김건우"))
                    .andExpect(jsonPath("$.result.email").value("gunwoo@naver.com"));

            verify(accountBookService, atLeastOnce()).getBook(any(),any());
        }


        @Test
        @DisplayName("최신순 정렬 페이징 조회")
        @WithMockUser
        public void 페이징_테스트() throws Exception {

            PageRequest pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "registeredAt");

            mockMvc.perform(get("/api/account_books")
                    .param("page", "0")
                    .param("size", "5")
                    .param("sort", "registeredAt")
                    .param("direction", "Sort.Direction.DESC"))
                    .andExpect(status().isOk());

            assertThat(pageable.getPageNumber()).isEqualTo(0);
            assertThat(pageable.getPageSize()).isEqualTo(5);
            assertThat(pageable.getSort()).isEqualTo(Sort.by("registeredAt").descending());
            verify(accountBookService, atLeastOnce()).getBooks(any(),any());
        }
    }


    @Nested
    @DisplayName("작성")
    class Insert {
        /**given**/
        AccountAddDto request = AccountAddDto.builder()
                .title("테스트 제목")
                .memo("테스트 내용")
                .balance(10000)
                .build();
        //예상 응답값
        AccountAddResponse response = AccountAddResponse.builder()
                .id(1L)
                .email("gunwoo@naver.com")
                .balance(10000)
                .title("테스트 제목")
                .userName("김건우")
                .build();

        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 성공")
        void 가계부_등록_성공() throws Exception {
            /**when**/
            when(accountBookService.makeBook(any(), any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").exists())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.email").value("gunwoo@naver.com"))
                    .andExpect(jsonPath("$.result.balance").value(10000));
            verify(accountBookService, atLeastOnce()).makeBook(any(), any());
        }


        @Test
        @WithAnonymousUser   // 인증 안된 상태
        @DisplayName("가계부 생성 실패 :권한 인증 없음")
        void 가계부_등록_실패1() throws Exception {
            /**when**/
            when(accountBookService.makeBook(any(), any())).thenThrow(new AccountException(ErrorCode.INVALID_PERMISSION));
            /**then**/
            String url = "/api/account_books";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
            verify(accountBookService, never()).makeBook(any(), any());
        }

    }

    @Nested
    @DisplayName("수정")
    class AccountBookUpdate {

        /**가계부 수정 시 Request**/
        AccountModifyDto request = AccountModifyDto.builder()
                .title("테스트 제목")
                .memo("테스트 내용")
                .balance(80000)
                .build();
        /**가계부 수정 시 Response**/
        AccountModifyResponse response = AccountModifyResponse.builder()
                .message("가계부 수정 완료")
                .updateBalance(80000)
                .updateMemo("테스트 내용")
                .build();
        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 수정 성공")
        void 가계부_수정_성공() throws Exception {
            /**when**/
            when(accountBookService.updateBook(any(), any(),any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").exists())
                    .andExpect(jsonPath("$.result.message").value("가계부 수정 완료"))
                    .andExpect(jsonPath("$.result.updateMemo").value("테스트 내용"))
                    .andExpect(jsonPath("$.result.updateBalance").value(80000));
            verify(accountBookService, times(1)).updateBook(any(), any(),any());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("가계부 수정 실패 : 인증되지 않은 사용자 수정 시도시")
        void 가계부_수정_실패() throws Exception {
            /**when**/
            when(accountBookService.updateBook(any(), any(),any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
            verify(accountBookService, never()).updateBook(any(), any(),any());
        }

        @Test
        @WithMockUser
        @DisplayName("가계부 수정 실패 : 수정 실패 USER 존재하지 않음")
        void 가계부_수정_실패2() throws Exception {
            /**when**/
            when(accountBookService.updateBook(any(), any(), any())).thenThrow(new UserException(ErrorCode.EMAIL_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"));
            verify(accountBookService, times(1)).updateBook(any(), any(),any());
        }

        @Test
        @WithMockUser
        @DisplayName("가계부 수정 실패 : 수정 실패 가계부 존재하지 않음")
        void 가계부_수정_실패3() throws Exception {
            /**when**/
            when(accountBookService.updateBook(any(), any(), any())).thenThrow(new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("ACCOUNTBOOK_NOT_FOUND"));
            verify(accountBookService, times(1)).updateBook(any(), any(),any());
        }

        @Test
        @WithMockUser
        @DisplayName("가계부 수정 실패 : 수정 실패 가계부 작성한 사람 != 수정 시도하는 사람")
        void 가계부_수정_실패4() throws Exception {
            /**when**/
            when(accountBookService.updateBook(any(), any(), any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"));
            verify(accountBookService, times(1)).updateBook(any(), any(),any());
        }
    }

    @Nested
    @DisplayName("삭제")
    class AccountBookDelete {

        /**
         * 가계부 삭제 시 Response
         **/
        AccountBookDeleteResponse response = AccountBookDeleteResponse.builder()
                .message("가계부 삭제 완료")
                .id(1L)
                .build();
        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 삭제 성공")
        void 가계부_삭제_성공() throws Exception {
            /**when**/
            when(accountBookService.deleteBook(any(), any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").exists())
                    .andExpect(jsonPath("$.result.message").value("가계부 삭제 완료"))
                    .andExpect(jsonPath("$.result.id").value(1L));

            verify(accountBookService, times(1)).deleteBook( any(),any());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("가계부 삭제 실패 : 인증되지 않은 user")
        void 가계부_삭제_실패1() throws Exception {
            /**when**/
            when(accountBookService.deleteBook(any(), any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(accountBookService, never()).deleteBook( any(),any());
        }


        @Test
        @WithMockUser
        @DisplayName("가계부 삭제 실패 :  user 존재하지 않음")
        void 가계부_삭제_실패2() throws Exception {
            /**when**/
            when(accountBookService.deleteBook(any(), any())).thenThrow(new UserException(ErrorCode.EMAIL_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"));
            verify(accountBookService, times(1)).deleteBook( any(),any());
        }

        @Test
        @WithMockUser
        @DisplayName("가계부 삭제 실패 : 삭제 실패 가계부 존재하지 않음")
        void 가계부_삭제_실패3() throws Exception {
            /**when**/
            when(accountBookService.deleteBook( any(), any())).thenThrow(new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("ACCOUNTBOOK_NOT_FOUND"));
            verify(accountBookService, times(1)).deleteBook(any(),any());
        }
    }
}
