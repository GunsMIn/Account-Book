package com.payhere.account.fixture;

import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.UserRole;

import java.sql.Timestamp;
import java.time.Instant;

public class UserFixture {

    public static User get(String email, String password) {
        return  User.builder()
                .id(1l)
                .name(email)
                .password(password)
                .role(UserRole.ROLE_USER)
                .build();

    }
}
