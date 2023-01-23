package com.payhere.account.domain.dto.accountBook;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class AccountModifyDto {
    @NotEmpty
    private String title;
    @Size(min = 5, max=20)
    private String memo;
    @Positive
    @Max(value = 10000000, message = "가계부에 천만원 이상 등록 할 수 없습니다.")
    private Integer balance;




}
