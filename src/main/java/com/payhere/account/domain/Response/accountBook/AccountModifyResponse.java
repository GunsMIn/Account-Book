package com.payhere.account.domain.Response.accountBook;

import com.payhere.account.domain.entity.AccountBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

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
    private String updateAt;

    /**엔티티 -> Response 변환 메서드**/
    public static AccountModifyResponse of(AccountBook accountBook) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        return AccountModifyResponse.builder()
                .message("가계부 수정 완료")
                .accountBookId(accountBook.getId())
                .updateTitle(accountBook.getTitle())
                .updateMemo(accountBook.getMemo())
                .updateBalance(accountBook.getBalance())
                .updateAt(sdf.format(accountBook.getUpdatedAt()))
                .build();
    }



}
