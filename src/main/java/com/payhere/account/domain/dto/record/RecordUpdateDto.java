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
public class RecordUpdateDto {
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


}
