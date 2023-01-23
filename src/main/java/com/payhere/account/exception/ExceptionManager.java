package com.payhere.account.exception;

import com.payhere.account.domain.Response.Response;
import com.payhere.account.exception.customException.AccountException;
import com.payhere.account.exception.customException.RecordException;
import com.payhere.account.exception.customException.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionManager {
    /**UserException**/
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userAppExceptionHandler(UserException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error("ERROR", errorResponse));

    }

    /**AccountException**/
    @ExceptionHandler(AccountException.class)
    public ResponseEntity<?> accountExceptionHandler(AccountException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error("ERROR", errorResponse));

    }

    /**RecordException**/
    @ExceptionHandler(RecordException.class)
    public ResponseEntity<?> recordExceptionHandler(RecordException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error("ERROR", errorResponse));

    }

    /**ValidatedException**/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validExceptionHandler(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);

    }





}