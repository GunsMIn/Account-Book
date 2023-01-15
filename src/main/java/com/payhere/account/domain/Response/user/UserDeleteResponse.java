package com.payhere.account.domain.Response.user;

import com.payhere.account.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDeleteResponse {
    private String message;
    private Long id;


    public static UserDeleteResponse of(Long id) {
       return UserDeleteResponse.builder()
                .message("회원 삭제 성공")
                .id(id)
                .build();
    }
}
