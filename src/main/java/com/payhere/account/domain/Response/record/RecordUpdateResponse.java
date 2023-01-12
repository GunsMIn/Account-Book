package com.payhere.account.domain.Response.record;

import com.payhere.account.domain.entity.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class RecordUpdateResponse {

    private String message;
    private Integer money;
    private String memo;
    private String act;
    private String expendType;
    private String day;

    public static RecordUpdateResponse of(Record record) {
        return RecordUpdateResponse.builder()
                .message("가계부 기록 수정 성공")
                .money(record.getMoney())
                .memo(record.getMemo())
                .act(record.getAct().getDescription())
                .expendType(record.getExpendType().getName())
                .day(record.getDay().getName())
                .build();

    }
}
