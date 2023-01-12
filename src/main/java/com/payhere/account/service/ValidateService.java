package com.payhere.account.service;

import com.payhere.account.domain.dto.user.UserJoinDto;
import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.exception.AccountException;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.RecordException;
import com.payhere.account.exception.UserException;
import com.payhere.account.repository.AccountBookRepository;
import com.payhere.account.repository.RecordRepository;
import com.payhere.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final UserRepository userRepository;
    private final AccountBookRepository accountBookRepository;
    private final RecordRepository recordRepository;

    /**authentication.getName() 으로 해당 user 유뮤 검사 메서드**/
    public User getUser(String email) {
        /*user 찾기*/
        return userRepository.findByEmail(email).orElseThrow(()
                -> new UserException(ErrorCode.EMAIL_NOT_FOUND, ErrorCode.EMAIL_NOT_FOUND.getMessage()));
    }
    /**가계부 id로 해당 AccountBook 유뮤 검사 메서드**/
    public AccountBook getAccountBook(Long id) {
        return accountBookRepository.findById(id).orElseThrow(()
                -> new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND, ErrorCode.ACCOUNTBOOK_NOT_FOUND.getMessage()));
    }


    /**기록 id로 해당 Record 유뮤 검사 메서드**/
    public Record getRecord(Long id) {
        return recordRepository.findById(id).orElseThrow(()
                -> new RecordException(ErrorCode.RECORD_NOT_FOUND, ErrorCode.RECORD_NOT_FOUND.getMessage()));
    }
    /**사용자 권한 check**/
    public void checkAuthority(User user, AccountBook accountBook) {
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION,ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }

    /**회원 가입시 email중복체크 메서드**/
    public void checkUserEmail(UserJoinDto userJoinDto) {
        userRepository.findByEmail(userJoinDto.getEmail())
                .ifPresent(user -> {
                    throw new UserException(ErrorCode.DUPLICATED_EMAIL,ErrorCode.DUPLICATED_EMAIL.getMessage());
                });
    }
}
