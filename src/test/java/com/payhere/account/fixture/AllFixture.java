package com.payhere.account.fixture;

import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.domain.entity.type.Day;
import com.payhere.account.domain.entity.type.ExpendType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;

@Getter
@Builder
public class AllFixture {
    //user
    private Long userId;
    private String userName;
    private String email;
    private String password;

    //accountBook
    private Long accountBookId;
    private String title;
    private Integer balance;

    //record
    private Long recordId;
    private String memo;
    private Integer money;
    private ExpendType expendType;
    private Act act;
    private Day day;

    public static AllFixture getDto() {
        return AllFixture.builder()
                .userId(1L)
                .userName("김건우")
                .email("gun@naver.com")
                .password("test1234")
                .accountBookId(1L)
                .title("김건우의 가계부")
                .balance(1000000)
                .recordId(1L)
                .memo("교통비로 1000원 지출")
                .money(1000)
                .act(Act.SPENDING)
                .expendType(ExpendType.TRANSPORT_EXPENSE)
                .day(Day.MONDAY)
                .build();
    }


}
