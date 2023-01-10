package com.payhere.account.domain.dto.accountBook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class AccountModifyRequest {

    private String title;
    private String memo;
    private Integer balance;




}
