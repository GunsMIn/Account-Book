package com.payhere.account.domain.Response.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserLoginResponse {
    private String jwt;
    private String refreshToken;

    public static UserLoginResponse of(String token, String refreshToken) {
        return UserLoginResponse.builder()
                .jwt(token)
                .refreshToken(refreshToken)
                .build();
    }

}