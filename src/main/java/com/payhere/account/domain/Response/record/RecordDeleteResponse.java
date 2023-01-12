package com.payhere.account.domain.Response.record;

import com.payhere.account.domain.Response.accountBook.AccountBookDeleteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
public class RecordDeleteResponse {

    private String message;
    private Long id;

    public static RecordDeleteResponse of(Long id) {
            return RecordDeleteResponse.builder()
                    .message("가계부 기록 삭제 완료")
                    .id(id)
                    .build();
        }
}
