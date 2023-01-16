package com.payhere.account.domain.entity.type;

import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.RecordException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Day {
    /**날짜 Enum**/
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");

    private final String name;

    /**String -> enum 메서드**/
    public static Day getEnum(String value) {
        if (value.equals(MONDAY.getName())) {
            return MONDAY;
        } else if (value.equals(TUESDAY.getName())) {
            return TUESDAY;
        }else if (value.equals(WEDNESDAY.getName())) {
            return WEDNESDAY;
        }else if (value.equals(THURSDAY.getName())) {
            return THURSDAY;
        }else if (value.equals(FRIDAY.getName())) {
            return FRIDAY;
        }else if (value.equals(SATURDAY.getName())) {
            return SATURDAY;
        }else if (value.equals(SUNDAY.getName())) {
            return SUNDAY;
        }else{
            throw new RecordException(ErrorCode.DAY_FAULT, ErrorCode.DAY_FAULT.getMessage());
        }

    }
}
