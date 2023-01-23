package com.payhere.account.domain.dto.accountBook;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.User;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class AccountAddDto {
    @NotBlank(message = "가계부의 제목은 필수 값입니다.")
    private String title;
    @Size(min = 5, max=20)
    private String memo;
    //잔고
    @Positive
    @Max(value = 10000000, message = "가계부에 천만원 이상 등록 할 수 없습니다.")
    private Integer balance;


    public AccountBook toEntity(User user) {
        return AccountBook.builder()
                .title(title)
                .memo(memo)
                .balance(balance)
                .user(user)
                .build();
    }

}
