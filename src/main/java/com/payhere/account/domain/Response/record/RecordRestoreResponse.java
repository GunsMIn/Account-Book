package com.payhere.account.domain.Response.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class RecordRestoreResponse {

    private String message;
    private Long id;

    public static RecordRestoreResponse of(Long id) {
        return RecordRestoreResponse.builder()
                .message("가계부 기록 복원 성공")
                .id(id).build();
    }
}
