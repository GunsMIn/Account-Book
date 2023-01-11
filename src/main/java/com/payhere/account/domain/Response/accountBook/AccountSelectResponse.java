package com.payhere.account.domain.Response.accountBook;

import com.payhere.account.domain.entity.AccountBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSelectResponse {

    private Long id;
    private String title;
    private String memo;
    private Integer balance;
    private String userName;
    private String email;

    /**entity -> response**/
    public static AccountSelectResponse of(AccountBook accountBook) {

        return AccountSelectResponse.builder()
                .id(accountBook.getId())
                .title(accountBook.getTitle())
                .memo(accountBook.getMemo())
                .balance(accountBook.getBalance())
                .userName(accountBook.getUser().getName())
                .email(accountBook.getUser().getEmail())
                .build();
    }

    /**Page<Entity> -> Page<response>**/
    public static Page<AccountSelectResponse> of(Page<AccountBook> accountBooks) {
        return accountBooks.map(a -> AccountSelectResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .memo(a.getMemo())
                .balance(a.getBalance())
                .userName(a.getUser().getName())
                .email(a.getUser().getEmail())
                .build());
    }

}
