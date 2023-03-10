package com.payhere.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payhere.account.config.encrypt.EncrypterConfig;
import com.payhere.account.domain.Response.accountBook.AccountSelectResponse;
import com.payhere.account.domain.Response.record.*;
import com.payhere.account.domain.dto.record.RecordDto;
import com.payhere.account.domain.dto.record.RecordUpdateDto;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.AccountException;
import com.payhere.account.exception.customException.RecordException;
import com.payhere.account.exception.customException.UserException;
import com.payhere.account.service.AccountBookService;
import com.payhere.account.service.RecordService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecordController.class)
class RecordControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    RecordService recordService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("????????? ?????? insert")
    class Insert {

        RecordDto request = RecordDto.builder()
                .day("???")
                .expendType("?????????")
                .money(1000)
                .act("??????")
                .build();
        RecordResponse response = RecordResponse.builder()
                .message("????????? ?????? ??????")
                .day("???")
                .expendType("?????????")
                .money(1000)
                .act("??????")
                .build();

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????() throws Exception {

            /**when**/
            when(recordService.spendOrSave(any(), any(),any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.expendType").exists())
                    .andExpect(jsonPath("$.result.expendType").value("?????????"))
                    .andExpect(jsonPath("$.result.act").value("??????"))
                    .andExpect(jsonPath("$.result.money").value(1000));
            verify(recordService, atLeastOnce()).spendOrSave(any(), any(),any());
        }


        @Test
        @WithAnonymousUser
        @DisplayName("????????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????1() throws Exception {

            /**when**/
            when(recordService.spendOrSave(any(), any(),any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
            verify(recordService, never()).spendOrSave(any(), any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : ?????? ??????")
        void ?????????_??????_??????_??????2() throws Exception {

            /**when**/
            when(recordService.spendOrSave(any(), any(), any())).thenThrow(new UserException(ErrorCode.EMAIL_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"));
            verify(recordService, atLeastOnce()).spendOrSave(any(), any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : ????????? ??????")
        void ?????????_??????_??????_??????3() throws Exception {

            /**when**/
            when(recordService.spendOrSave(any(), any(), any())).thenThrow(new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("ACCOUNTBOOK_NOT_FOUND"));
            verify(recordService, atLeastOnce()).spendOrSave(any(), any(),any());
        }
    }


    @Nested
    @DisplayName("????????? ?????? Update(??????)")
    class Update {

        RecordUpdateDto request = RecordUpdateDto.builder()
                .expendType("?????????")
                .act("??????")
                .day("???")
                .memo("????????? ????????? 1000???")
                .money(1000)
                .build();

        RecordUpdateResponse response = RecordUpdateResponse.builder()
                .message("?????? ?????? ??????")
                .expendType("?????????")
                .act("??????")
                .day("???")
                .memo("????????? ????????? 1000???")
                .money(1000)
                .build();

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????() throws Exception {
            /**when**/
            when(recordService.updateRecord(any(),any(),any(),any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.expendType").exists())
                    .andExpect(jsonPath("$.result.expendType").value("?????????"))
                    .andExpect(jsonPath("$.result.act").value("??????"))
                    .andExpect(jsonPath("$.result.day").value("???"))
                    .andExpect(jsonPath("$.result.money").value(1000))
                    .andExpect(jsonPath("$.result.message").value("?????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.memo").value("????????? ????????? 1000???"));

            verify(recordService, atLeastOnce()).updateRecord(any(), any(),any(),any());
        }

        @Test
        @WithAnonymousUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : ???????????? ?????? ???????????? ?????? ?????????")
        void ?????????_??????_??????_??????1() throws Exception {
            /**when**/
            when(recordService.updateRecord(any(),any(),any(),any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(recordService, never()).updateRecord(any(), any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : user ??????")
        void ?????????_??????_??????_??????2() throws Exception {
            /**when**/
            when(recordService.updateRecord(any(),any(),any(),any())).thenThrow(new UserException(ErrorCode.EMAIL_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"));

            verify(recordService, atLeastOnce()).updateRecord(any(), any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : ????????? ??????")
        void ?????????_??????_??????_??????3() throws Exception {
            /**when**/
            when(recordService.updateRecord(any(),any(),any(),any())).thenThrow(new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("ACCOUNTBOOK_NOT_FOUND"));

            verify(recordService, atLeastOnce()).updateRecord(any(), any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : ????????? ?????? ??????")
        void ?????????_??????_??????_??????4() throws Exception {
            /**when**/
            when(recordService.updateRecord(any(),any(),any(),any())).thenThrow(new RecordException(ErrorCode.RECORD_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("RECORD_NOT_FOUND"));

            verify(recordService, atLeastOnce()).updateRecord(any(), any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : ????????? ?????? ??? user != ?????? ??????????????? user")
        void ?????????_??????_??????_??????5() throws Exception {
            /**when**/
            when(recordService.updateRecord(any(),any(),any(),any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(patch(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"));

            verify(recordService, times(1)).updateRecord(any(), any(),any(),any());
        }

    }


    @Nested
    @DisplayName("????????? ?????? delete")
    class Delete {
        /**?????? ?????? ??? ?????????**/
       RecordDeleteResponse response = RecordDeleteResponse.builder()
                .message("?????? ?????? ??????")
                .id(1L)
                .build();

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????() throws Exception {
            /**when**/
            when(recordService.deleteRecord(any(), any(), any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").exists())
                    .andExpect(jsonPath("$.result.message").value("?????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.id").value(1L));

            verify(recordService, atLeastOnce()).deleteRecord(any(),any(),any());
        }

        @Test
        @WithAnonymousUser   // ???????????? ?????? ?????????
        @DisplayName("????????? ?????? ?????? ?????? : ???????????? ?????? ?????????")
        void ?????????_??????_??????_??????1() throws Exception {
            /**when**/
            when(recordService.deleteRecord(any(), any(), any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(recordService, never()).deleteRecord(any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : user ???????????? ??????")
        void ?????????_??????_??????_??????2() throws Exception {
            /**when**/
            when(recordService.deleteRecord(any(), any(), any())).thenThrow(new UserException(ErrorCode.EMAIL_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"));

            verify(recordService, times(1)).deleteRecord(any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : AccountBook(?????????) ???????????? ??????")
        void ?????????_??????_??????_??????3() throws Exception {
            /**when**/
            when(recordService.deleteRecord(any(), any(), any())).thenThrow(new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("ACCOUNTBOOK_NOT_FOUND"));

            verify(recordService, times(1)).deleteRecord(any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : Record(????????? ??????) ???????????? ??????")
        void ?????????_??????_??????_??????4() throws Exception {
            /**when**/
            when(recordService.deleteRecord(any(), any(), any())).thenThrow(new RecordException(ErrorCode.RECORD_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("RECORD_NOT_FOUND"));

            verify(recordService, times(1)).deleteRecord(any(),any(),any());
        }

        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ?????? ?????? : Record ????????? ?????? != ?????? ??????????????? ??????")
        void ?????????_??????_??????_??????5() throws Exception {
            /**when**/
            when(recordService.deleteRecord(any(), any(), any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            /**then**/
            String url = "/api/account_books/1/records/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"));

                    verify(recordService, times(1)).deleteRecord(any(),any(),any());
        }
    }

    @Nested
    @DisplayName("????????? ?????? restore(??????)")
    class Restore {
        /**?????? ?????? Response**/
        RecordRestoreResponse response = RecordRestoreResponse.builder()
                .message("?????? ?????? ??????")
                .id(1L)
                .build();

        @Test
        @WithMockUser
        @DisplayName("?????? ?????? ??????")
        void ?????????_??????_??????_??????() throws Exception {
            /**when**/
            when(recordService.restoreRecord(any(), any(), any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records/1/restore";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").exists())
                    .andExpect(jsonPath("$.result.message").value("?????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.id").value(1L));

            verify(recordService, atLeastOnce()).restoreRecord(any(), any(), any());

        }

        @Test
        @WithAnonymousUser
        @DisplayName("?????? ?????? ?????? : ???????????? ?????? ?????????")
        void ?????????_??????_??????_??????1() throws Exception {
            /**when**/
            when(recordService.restoreRecord(any(), any(), any())).thenReturn(response);
            /**then**/
            String url = "/api/account_books/1/records/1/restore";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(recordService, never()).restoreRecord(any(), any(), any());

        }


        @Test
        @WithMockUser
        @DisplayName("?????? ?????? ?????? : ?????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????2() throws Exception {
            /**when**/
            when(recordService.restoreRecord(any(), any(), any())).thenThrow(new UserException(ErrorCode.USER_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1/restore";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("USER_NOT_FOUND"));

            verify(recordService, times(1)).restoreRecord(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("?????? ?????? ?????? : ????????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????3() throws Exception {
            /**when**/
            when(recordService.restoreRecord(any(), any(), any())).thenThrow(new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1/restore";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("ACCOUNTBOOK_NOT_FOUND"));

            verify(recordService, times(1)).restoreRecord(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("?????? ?????? ?????? : ????????? ?????? ?????? ?????? ??????")
        void ?????????_??????_??????_??????4() throws Exception {
            /**when**/
            when(recordService.restoreRecord(any(), any(), any())).thenThrow(new RecordException(ErrorCode.RECORD_NOT_FOUND));
            /**then**/
            String url = "/api/account_books/1/records/1/restore";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("RECORD_NOT_FOUND"));

            verify(recordService, times(1)).restoreRecord(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("?????? ?????? ?????? : ????????? ?????? ????????? ?????? != ??????????????? ??????")
        void ?????????_??????_??????_??????5() throws Exception {
            /**when**/
            when(recordService.restoreRecord(any(), any(), any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            /**then**/
            String url = "/api/account_books/1/records/1/restore";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"));

            verify(recordService, times(1)).restoreRecord(any(), any(), any());
        }

    }

    @Nested
    @DisplayName("????????? ?????? ??????/????????? ?????? ?????????")
    class Select {


        @Test
        @DisplayName("?????? ????????? ?????? ????????? ??????")
        @WithMockUser
        public void ??????_?????????_?????????() throws Exception {

            PageRequest pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "registeredAt");

            mockMvc.perform(get("/api/account_books/1/records")
                    .param("page", "0")
                    .param("size", "5")
                    .param("sort", "registeredAt")
                    .param("direction", "Sort.Direction.DESC"))
                    .andExpect(status().isOk());

            assertThat(pageable.getPageNumber()).isEqualTo(0);
            assertThat(pageable.getPageSize()).isEqualTo(5);
            assertThat(pageable.getSort()).isEqualTo(Sort.by("registeredAt").descending());
            verify(recordService, atLeastOnce()).getRecords(any(), any(), any());
        }


    }
}