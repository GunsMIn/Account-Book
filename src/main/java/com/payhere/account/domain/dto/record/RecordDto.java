package com.payhere.account.domain.dto.record;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.domain.entity.type.Day;
import com.payhere.account.domain.entity.type.ExpendType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RecordDto {
    @NotBlank
    private String memo;

    @Positive //돈(money)는 로직을 통해서 + , - 처리
    @NotNull
    private Integer money;

    @NotBlank
    private String act;

    @NotBlank
    private String expendType;

    @NotBlank
    private String day;


    public Record toEntity(User user , AccountBook accountBook, String act, String expendType, String day) {
        /**ExpendType.getEnum(expendType); : string을 enum 타입으로**/

        return Record.builder()
                .memo(memo)
                .money(money)
                .act(Act.getEnum(act))
                .expendType(ExpendType.getEnum(expendType))
                .day(Day.getEnum(day))
                .user(user)
                .accountBook(accountBook)
                .build();


    }



}
