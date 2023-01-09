package com.payhere.account.domain.dto.user;

import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.UserRole;
import lombok.*;

import javax.validation.constraints.Email;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UserJoinRequest {
    private String userName;
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;
    private String password;

    public User toEntity(String password) {
        return User.builder()
                .userName(userName)
                .email(email)
                .password(password)
                .role(UserRole.ROLE_USER)
                .build();
    }

}