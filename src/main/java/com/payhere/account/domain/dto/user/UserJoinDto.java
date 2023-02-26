package com.payhere.account.domain.dto.user;

import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.UserRole;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class UserJoinDto {

    @NotBlank
    private String userName;

    @NotNull
    @Email
    private String email;

    @NotBlank
    private String password;

    public User toEntity(String encodePassword) {
        return User.builder()
                .name(userName)
                .email(email)
                .password(encodePassword)
                .role(UserRole.USER)
                .build();
    }

    public User toEntityOfAdmin(String encodePassword) {
        return User.builder()
                .name(userName)
                .email(email)
                .password(encodePassword)
                .role(UserRole.ADMIN)
                .build();
    }

}