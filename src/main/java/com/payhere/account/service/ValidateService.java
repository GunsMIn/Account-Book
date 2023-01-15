package com.payhere.account.service;

import com.payhere.account.domain.dto.user.UserJoinDto;
import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.exception.customException.AccountException;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.RecordException;
import com.payhere.account.exception.customException.UserException;
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

    /**
     * USER 반환 : authentication.getName() 으로 해당 user 유뮤 검사 메서드
     *
     * @param email  이메일로 해당 User 조회
     * @return User반환
     *
     * User 존재 하지 않을 시 HttpStatus.NOT_FOUND(404) 상태코드 반환
     */
    public User getUser(String email) {
        /*user 찾기*/
        return userRepository.findByEmail(email).orElseThrow(()
                -> new UserException(ErrorCode.EMAIL_NOT_FOUND, ErrorCode.EMAIL_NOT_FOUND.getMessage()));
    }

    /**
     * AccountBook 반환 : 가계부 id로 해당 AccountBook 유뮤 검사 메서드
     *
     * @param id  id로 해당 가계부 조회
     * @return AccountBook 반환
     *
     * AccountBook 존재 하지 않을 시 HttpStatus.NOT_FOUND(404) 상태코드 반환
     */
    public AccountBook getAccountBook(Long id) {
        return accountBookRepository.findById(id).orElseThrow(()
                -> new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND, ErrorCode.ACCOUNTBOOK_NOT_FOUND.getMessage()));
    }

    /**
     * Record 반환 : 기록 id로 해당 Record 유뮤 검사 메서드
     *
     * @param id  id로 해당 가계부 기록 조회
     * @return Record 반환
     *
     * Record 존재 하지 않을 시 HttpStatus.NOT_FOUND(404) 상태코드 반환
     */
    public Record getRecord(Long id) {
        return recordRepository.findById(id).orElseThrow(()
                -> new RecordException(ErrorCode.RECORD_NOT_FOUND, ErrorCode.RECORD_NOT_FOUND.getMessage()));
    }

    /**
     * 사용자 권한 check 검사 메서드
     *
     * @param user  -> 조회된  user
     * @param accountBook -> 조회된 accountBook
     *
     * user와 accountBook을 작성한 user의 권한 check 메서드
     *
     * 권한이 없을 시 시 HttpStatus.UNAUTHORIZED(401) 상태코드 반환
     */
    public void checkAuthority(User user, AccountBook accountBook) {
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION,ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }

    /**
     * 회원가입 시 email 중복 검사 메서드
     *
     * @param userJoinDto  -> userAJoinDto.getEmail()로 해당 email의 사용자(user)가 존재하는지 확인
     *
     * email이 중복 될 시 HttpStatus.CONFLICT(409) 상태코드 반환
     */
    public void checkUserEmail(UserJoinDto userJoinDto) {
        userRepository.findByEmail(userJoinDto.getEmail())
                .ifPresent(user -> {
                    throw new UserException(ErrorCode.DUPLICATED_EMAIL,ErrorCode.DUPLICATED_EMAIL.getMessage());
                });
    }
}
