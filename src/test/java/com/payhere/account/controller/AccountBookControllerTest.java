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
    @DisplayName("????????? ?????? ?????????")
    class select {

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????() throws Exception {
            Long id = 1L;
            //?????? ??? ??????
            AccountSelectResponse response = AccountSelectResponse.builder()
                    .id(id)
                    .balance(100000)
                    .title("????????? ??????")
                    .memo("????????? ??????")
                    .userName("?????????")
                    .email("gunwoo@naver.com")
                    .build();

            //Service??? ?????? ????????? ????????? post entity ??????
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
                    .andExpect(jsonPath("$.result.title").value("????????? ??????"))
                    .andExpect(jsonPath("$.result.memo").value("????????? ??????"))
                    .andExpect(jsonPath("$.result.userName").value("?????????"))
                    .andExpect(jsonPath("$.result.email").value("gunwoo@naver.com"));

            verify(accountBookService, atLeastOnce()).getBook(any(),any());
        }


        @Test
        @DisplayName("????????? ?????? ????????? ??????")
        @WithMockUser
        public void ?????????_?????????() throws Exception {

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
    @DisplayName("????????? ?????? ?????????")
    class Insert {
        /**given**/
        AccountAddDto request = AccountAddDto.builder()
                .title("????????? ??????")
                .memo("????????? ??????")
                .balance(10000)
                .build();
        //?????? ?????????
        AccountAddResponse response = AccountAddResponse.builder()
                .id(1L)
                .email("gunwoo@naver.com")
                .balance(10000)
                .title("????????? ??????")
                .userName("?????????")
                .build();

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ??????")
        void ?????????_??????_??????() throws Exception {
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
        @WithAnonymousUser   // ?????? ?????? ??????
        @DisplayName("????????? ?????? ?????? :?????? ?????? ??????")
        void ?????????_??????_??????1() throws Exception {
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
    @DisplayName("????????? ?????? ?????????")
    class AccountBookUpdate {

        /**????????? ?????? ??? Request**/
        AccountModifyDto request = AccountModifyDto.builder()
                .title("????????? ??????")
                .memo("????????? ??????")
                .balance(80000)
                .build();
        /**????????? ?????? ??? Response**/
        AccountModifyResponse response = AccountModifyResponse.builder()
                .message("????????? ?????? ??????")
                .updateBalance(80000)
                .updateMemo("????????? ??????")
                .build();
        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ??????")
        void ?????????_??????_??????() throws Exception {
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
                    .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.updateMemo").value("????????? ??????"))
                    .andExpect(jsonPath("$.result.updateBalance").value(80000));
            verify(accountBookService, times(1)).updateBook(any(), any(),any());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("????????? ?????? ?????? : ???????????? ?????? ????????? ?????? ?????????")
        void ?????????_??????_??????() throws Exception {
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
        @DisplayName("????????? ?????? ?????? : ?????? ?????? USER ???????????? ??????")
        void ?????????_??????_??????2() throws Exception {
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
        @DisplayName("????????? ?????? ?????? : ?????? ?????? ????????? ???????????? ??????")
        void ?????????_??????_??????3() throws Exception {
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
        @DisplayName("????????? ?????? ?????? : ?????? ?????? ????????? ????????? ?????? != ?????? ???????????? ??????")
        void ?????????_??????_??????4() throws Exception {
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
    @DisplayName("????????? ?????? ?????????")
    class AccountBookDelete {

        /**
         * ????????? ?????? ??? Response
         **/
        AccountBookDeleteResponse response = AccountBookDeleteResponse.builder()
                .message("????????? ?????? ??????")
                .id(1L)
                .build();
        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ??????")
        void ?????????_??????_??????() throws Exception {
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
                    .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.id").value(1L));

            verify(accountBookService, times(1)).deleteBook( any(),any());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("????????? ?????? ?????? : ???????????? ?????? user")
        void ?????????_??????_??????1() throws Exception {
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
        @DisplayName("????????? ?????? ?????? :  user ???????????? ??????")
        void ?????????_??????_??????2() throws Exception {
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
        @DisplayName("????????? ?????? ?????? : ?????? ?????? ????????? ???????????? ??????")
        void ?????????_??????_??????3() throws Exception {
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
