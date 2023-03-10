package com.payhere.account.domain.entity.type;

import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.RecordException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Act {
    SPENDING("지출"), SAVING("저축");
    private final String description;

    /**String -> enum 메서드**/
    public static Act getEnum(String value) {
        if (value.equals(SPENDING.getDescription())) {
            return SPENDING;
        } else if (value.equals(SAVING.getDescription())) {
            return SAVING;
        }else{
            throw new RecordException(ErrorCode.RECORD_FAULT, ErrorCode.RECORD_FAULT.getMessage());
        }

    }
}
