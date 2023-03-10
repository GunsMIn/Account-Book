package com.payhere.account.domain.Response.user;

import com.payhere.account.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class UserJoinResponse {
    private Long userId;
    private String userName;
    private String email;

    public static UserJoinResponse of(User user){
        return UserJoinResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .build();
    }
}