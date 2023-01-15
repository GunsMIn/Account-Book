package com.payhere.account.domain.dto.record;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.domain.entity.type.Day;
import com.payhere.account.domain.entity.type.ExpendType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RecordUpdateDto {

    private String memo;

    private Integer money;

    private String act;

    private String expendType;

    private String day;


}
