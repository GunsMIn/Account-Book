package com.payhere.account.domain.Response.user;

import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserAdminResponse {

    private Long userId;
    private String userName;
    private UserRole role;
    private String email;

    public static UserAdminResponse of(User user) {
        return UserAdminResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
