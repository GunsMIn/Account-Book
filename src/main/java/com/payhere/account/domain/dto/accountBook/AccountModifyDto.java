package com.payhere.account.domain.dto.accountBook;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class AccountModifyDto {

    private String title;
    private String memo;
    private Integer balance;




}
