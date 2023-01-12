package com.payhere.account.domain.Response.record;

import com.payhere.account.domain.entity.Record;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecordListResponse {
    private Long id;
    private Integer money;
    private String memo;
    private String act;
    private String expendType;
    private String day;
    private String createdAt;

    /** Page<Entity> -> Page<Response> 변환처리 **/
    public static Page<RecordListResponse> of(Page<Record> record){
        Page<RecordListResponse> responses =
                record.map(r -> RecordListResponse.builder()
                        .id(r.getId())
                        .money(r.getMoney())
                        .memo(r.getMemo())
                        .act(r.getAct().getDescription())
                        .day(r.getDay().getName())
                        .createdAt(String.valueOf(r.getRegisteredAt()))
                        .build());
        return responses;
    }
}
