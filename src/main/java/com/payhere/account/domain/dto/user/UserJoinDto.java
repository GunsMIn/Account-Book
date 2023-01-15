package com.payhere.account.domain.dto.user;

import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class UserJoinDto {
    private String userName;
    private String email;
    private String password;

    public User toEntity(String encodePassword) {
        return User.builder()
                .name(userName)
                .email(email)
                .password(encodePassword)
                .role(UserRole.ROLE_USER)
                .build();
    }

    public User toEntityOfAdmin(String encodePassword) {
        return User.builder()
                .name(userName)
                .email(email)
                .password(encodePassword)
                .role(UserRole.ROLE_ADMIN)
                .build();
    }

}