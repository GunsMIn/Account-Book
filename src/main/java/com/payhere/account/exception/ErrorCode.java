package com.payhere.account.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "Email이 중복됩니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 user를 찾을 수 없습니다."),
    ACCOUNTBOOK_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 가계부를 찾을 수 없습니다"),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 가계부의 기록을 찾을 수 없습니다"),
    RECORD_FAULT(HttpStatus.NOT_ACCEPTABLE,"해당 가계부의 기록 타입은 지출/저축을 선택하셔야 합니다"),
    ACT_FAULT(HttpStatus.NOT_ACCEPTABLE,"저축 / 지출로 입력해주세요"),
    EXPEND_FAULT(HttpStatus.NOT_ACCEPTABLE,"해당하는 ExpendType이 없습니다 "),
    DAY_FAULT(HttpStatus.NOT_ACCEPTABLE,"해당하는 요일이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저는 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"),
    USER_ROLE_NOT_FOUND(HttpStatus.NOT_ACCEPTABLE, "해당 UserRole은 존재하지 않습니다.");

    private HttpStatus status;
    private String message;

}