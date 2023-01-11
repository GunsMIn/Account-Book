package com.payhere.account.domain.dto.accountBook;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter @ToString
public class AccountAddDto {

    private String title;

    private String memo;
    //잔고
    private Integer balance;


    public AccountBook toEntity(User user) {
        return AccountBook.builder()
                .title(title)
                .memo(memo)
                .balance(balance)
                .user(user)
                .build();
    }

}
