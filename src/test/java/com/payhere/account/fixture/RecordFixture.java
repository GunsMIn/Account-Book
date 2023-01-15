package com.payhere.account.fixture;

import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.domain.entity.type.Day;
import com.payhere.account.domain.entity.type.ExpendType;

public class RecordFixture {

    public static Record get(User user, AccountBook accountBook) {
        return Record.builder()
                .id(1L)
                .day(Day.MONDAY)
                .expendType(ExpendType.TRANSPORT_EXPENSE)
                .accountBook(accountBook)
                .user(user)
                .act(Act.SPENDING)
                .memo("교통비로 1000원 지출")
                .money(1000)
                .build();
    }
}
