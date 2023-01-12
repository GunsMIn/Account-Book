package com.payhere.account.domain.Response.record;

import com.payhere.account.domain.entity.Record;
import lombok.Builder;

@Builder
public class RecordSelectResponse {

    private Long id;
    private Integer money;
    private String memo;
    private String act;
    private String expendType;
    private String day;
    private String userName; // 가계부 기록 한 user이름
    private String accountBookTitle; // 가계부 제목

    public static RecordSelectResponse of(Record record) {
        return RecordSelectResponse.builder()
                .id(record.getId())
                .money(record.getMoney())
                .memo(record.getMemo())
                .act(record.getAct().getDescription())
                .expendType(record.getExpendType().getName())
                .day(record.getDay().getName())
                .userName(record.getUser().getName())
                .accountBookTitle(record.getAccountBook().getTitle())
                .build();

    }
}
