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
    @DisplayName("가계부 기록 insert")
    class Insert {

        RecordDto request = RecordDto.builder()
                .day("월")
                .expendType("교통비")
                .money(1000)
                .act("지출")
                .build();
        RecordResponse response = RecordResponse.builder()
                .message("가계부 기록 성공")
                .day("월")
                .expendType("교통비")
                .money(1000)
                .act("지출")
                .build();

        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 쓰기 성공")
        void 가계부_기록_등록_성공() throws Exception {

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
                    .andExpect(jsonPath("$.result.expendType").value("교통비"))
                    .andExpect(jsonPath("$.result.act").value("지출"))
                    .andExpect(jsonPath("$.result.money").value(1000));
            verify(recordService, atLeastOnce()).spendOrSave(any(), any(),any());
        }


        @Test
        @WithAnonymousUser
        @DisplayName("가계부 기록 쓰기 실패")
        void 가계부_기록_등록_실패1() throws Exception {

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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 쓰기 실패 : 유저 없음")
        void 가계부_기록_등록_실패2() throws Exception {

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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 쓰기 실패 : 가계부 없음")
        void 가계부_기록_등록_실패3() throws Exception {

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
    @DisplayName("가계부 기록 Update(수정)")
    class Update {

        RecordUpdateDto request = RecordUpdateDto.builder()
                .expendType("교통비")
                .act("지출")
                .day("월")
                .memo("지하철 교통비 1000원")
                .money(1000)
                .build();

        RecordUpdateResponse response = RecordUpdateResponse.builder()
                .message("기록 수정 성공")
                .expendType("교통비")
                .act("지출")
                .day("월")
                .memo("지하철 교통비 1000원")
                .money(1000)
                .build();

        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 수정 성공")
        void 가계부_기록_수정_성공() throws Exception {
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
                    .andExpect(jsonPath("$.result.expendType").value("교통비"))
                    .andExpect(jsonPath("$.result.act").value("지출"))
                    .andExpect(jsonPath("$.result.day").value("월"))
                    .andExpect(jsonPath("$.result.money").value(1000))
                    .andExpect(jsonPath("$.result.message").value("기록 수정 성공"))
                    .andExpect(jsonPath("$.result.memo").value("지하철 교통비 1000원"));

            verify(recordService, atLeastOnce()).updateRecord(any(), any(),any(),any());
        }

        @Test
        @WithAnonymousUser   // 인증된 상태
        @DisplayName("가계부 기록 수정 실패 : 인증되지 않은 사용자가 수정 시도시")
        void 가계부_기록_수정_실패1() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 수정 실패 : user 없음")
        void 가계부_기록_수정_실패2() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 수정 실패 : 가계부 없음")
        void 가계부_기록_수정_실패3() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 수정 실패 : 가계부 기록 없음")
        void 가계부_기록_수정_실패4() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 수정 실패 : 가계부 기록 쓴 user != 수정 시도하려는 user")
        void 가계부_기록_수정_실패5() throws Exception {
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
    @DisplayName("가계부 기록 delete")
    class Delete {
        /**기록 삭제 시 응답값**/
       RecordDeleteResponse response = RecordDeleteResponse.builder()
                .message("기록 수정 성공")
                .id(1L)
                .build();

        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 삭제 성공")
        void 가계부_기록_삭제_성공() throws Exception {
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
                    .andExpect(jsonPath("$.result.message").value("기록 수정 성공"))
                    .andExpect(jsonPath("$.result.id").value(1L));

            verify(recordService, atLeastOnce()).deleteRecord(any(),any(),any());
        }

        @Test
        @WithAnonymousUser   // 인증되지 않은 사용자
        @DisplayName("가계부 기록 삭제 실패 : 인증되지 않은 사용자")
        void 가계부_기록_삭제_실패1() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 삭제 실패 : user 존재하지 않음")
        void 가계부_기록_삭제_실패2() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 삭제 실패 : AccountBook(가계부) 존재하지 않음")
        void 가계부_기록_삭제_실패3() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 삭제 실패 : Record(가계부 기록) 존재하지 않음")
        void 가계부_기록_삭제_실패4() throws Exception {
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
        @WithMockUser   // 인증된 상태
        @DisplayName("가계부 기록 삭제 실패 : Record 작성한 유저 != 기록 삭제하려는 유저")
        void 가계부_기록_삭제_실패5() throws Exception {
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
    @DisplayName("가계부 기록 restore(복원)")
    class Restore {
        /**기록 복원 Response**/
        RecordRestoreResponse response = RecordRestoreResponse.builder()
                .message("기록 복원 성공")
                .id(1L)
                .build();

        @Test
        @WithMockUser
        @DisplayName("기록 복원 성공")
        void 가계부_기록_복원_성공() throws Exception {
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
                    .andExpect(jsonPath("$.result.message").value("기록 복원 성공"))
                    .andExpect(jsonPath("$.result.id").value(1L));

            verify(recordService, atLeastOnce()).restoreRecord(any(), any(), any());

        }

        @Test
        @WithAnonymousUser
        @DisplayName("기록 복원 실패 : 인증되지 않은 사용자")
        void 가계부_기록_복원_실패1() throws Exception {
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
        @DisplayName("기록 복원 실패 : 유저 존재 하지 않음")
        void 가계부_기록_복원_실패2() throws Exception {
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
        @DisplayName("기록 복원 실패 : 가계부 존재 하지 않음")
        void 가계부_기록_복원_실패3() throws Exception {
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
        @DisplayName("기록 복원 실패 : 가계부 기록 존재 하지 않음")
        void 가계부_기록_복원_실패4() throws Exception {
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
        @DisplayName("기록 복원 실패 : 가계부 기록 삭제한 유저 != 복원하려는 유저")
        void 가계부_기록_복원_실패5() throws Exception {
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
    @DisplayName("가계부 기록 조회/페이징 조회 테스트")
    class Select {


        @Test
        @DisplayName("기록 최신순 정렬 페이징 조회")
        @WithMockUser
        public void 기록_페이징_테스트() throws Exception {

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