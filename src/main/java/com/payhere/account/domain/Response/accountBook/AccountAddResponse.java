package com.payhere.account.domain.Response.accountBook;

import com.payhere.account.domain.entity.AccountBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class AccountAddResponse {
    private Long id;
    private String title;
    private Integer balance;
    private String userName;
    private String email;
    private String createdAt;

    /**엔티티 -> Response 변환 메서드**/
    public static AccountAddResponse of(AccountBook accountBook) {
        return AccountAddResponse.builder()
                .id(accountBook.getId())
                .title(accountBook.getTitle())
                .balance(accountBook.getBalance())
                .userName(accountBook.getUser().getName())
                .email(accountBook.getUser().getEmail())
                .createdAt(accountBook.getRegisteredAt())
                .build();
    }
}
