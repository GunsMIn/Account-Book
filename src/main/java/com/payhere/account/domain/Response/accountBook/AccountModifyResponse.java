package com.payhere.account.domain.Response.accountBook;

import com.payhere.account.domain.entity.AccountBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountModifyResponse {

    private String message;
    private Long accountBookId;
    private String updateTitle;
    private String updateMemo;
    private Integer updateBalance;

    /**엔티티 -> Response 변환 메서드**/
    public static AccountModifyResponse of(AccountBook accountBook) {
        return AccountModifyResponse.builder()
                .message("가계부 수정 완료")
                .accountBookId(accountBook.getId())
                .updateTitle(accountBook.getTitle())
                .updateMemo(accountBook.getMemo())
                .updateBalance(accountBook.getBalance())
                .build();
    }



}
