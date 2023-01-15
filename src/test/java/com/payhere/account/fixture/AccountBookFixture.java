package com.payhere.account.fixture;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.User;

public class AccountBookFixture {

    public static AccountBook get(String email, String password) {
        return AccountBook.builder()
                .id(1l)
                .user(UserFixture.get(email, password))
                .title("김건우의 가계부")
                .balance(1000000)
                .build();
    }

    public static AccountBook get(User user) {
        return AccountBook.builder()
                .id(1l)
                .user(user)
                .title("김건우의 가계부")
                .balance(1000000)
                .build();
    }
}
