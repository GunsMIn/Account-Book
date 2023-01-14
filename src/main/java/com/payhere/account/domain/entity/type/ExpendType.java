package com.payhere.account.domain.entity.type;

import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.RecordException;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ExpendType {

    FOOD_EXPENSE("식비"),
    LIVING_EXPENSE("생활용품비"),
    TRANSPORT_EXPENSE("교통비"),
    CLOTHING_EXPENSE("의류비"),
    HOSPITAL_EXPENSE("병원비"),
    ENTERTAIN_EXPENSE("유흥비"),
    CHILDCARE_EXPENSE("놀이비"),
    PHONE_EXPENSE("통신비"),
    UTILITY_EXPENSE("공과금"),
    ETC_EXPENSE("기타비용");

    private final String name;

    public static ExpendType getEnum(String value) {
        if (value.equals(FOOD_EXPENSE.getName())) {
            return FOOD_EXPENSE;
        } else if (value.equals(LIVING_EXPENSE.getName())) {
            return LIVING_EXPENSE;
        }else if (value.equals(TRANSPORT_EXPENSE.getName())) {
            return TRANSPORT_EXPENSE;
        }else if (value.equals(CLOTHING_EXPENSE.getName())) {
            return CLOTHING_EXPENSE;
        }else if (value.equals(HOSPITAL_EXPENSE.getName())) {
            return HOSPITAL_EXPENSE;
        }else if (value.equals(ENTERTAIN_EXPENSE.getName())) {
            return ENTERTAIN_EXPENSE;
        }else if (value.equals(CHILDCARE_EXPENSE.getName())) {
            return CHILDCARE_EXPENSE;
        }else if (value.equals(PHONE_EXPENSE.getName())) {
            return PHONE_EXPENSE;
        }else if (value.equals(UTILITY_EXPENSE.getName())) {
            return UTILITY_EXPENSE;
        }else if (value.equals(ETC_EXPENSE.getName())) {
            return ETC_EXPENSE;
        }else{
            throw new RecordException(ErrorCode.EXPEND_FAULT, ErrorCode.EXPEND_FAULT.getMessage());
        }

    }


}
