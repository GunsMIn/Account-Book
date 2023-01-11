package com.payhere.account.domain.Response.accountBook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountBookDeleteResponse {

    private String message;
    private Long id;

    public static AccountBookDeleteResponse of(Long id) {
        //return new AccountBookDeleteResponse("가계부 삭제 완료", id);
        return AccountBookDeleteResponse.builder()
                .message("가계부 삭제 완료")
                .id(id)
                .build();
    }

}
