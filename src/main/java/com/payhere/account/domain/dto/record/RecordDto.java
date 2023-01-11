package com.payhere.account.domain.dto.record;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.domain.entity.type.Day;
import com.payhere.account.domain.entity.type.ExpendType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RecordDto {

    private String memo;

    private Integer money;

    private String act;

    private String expendType;

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
