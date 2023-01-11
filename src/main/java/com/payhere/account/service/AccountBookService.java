package com.payhere.account.service;

import com.payhere.account.domain.Response.accountBook.AccountAddResponse;
import com.payhere.account.domain.Response.accountBook.AccountBookDeleteResponse;
import com.payhere.account.domain.Response.accountBook.AccountModifyResponse;
import com.payhere.account.domain.Response.accountBook.AccountSelectResponse;
import com.payhere.account.domain.dto.accountBook.AccountAddDto;
import com.payhere.account.domain.dto.accountBook.AccountModifyDto;
import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.User;
import com.payhere.account.exception.AccountException;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.UserException;
import com.payhere.account.repository.AccountBookRepository;
import com.payhere.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountBookService {

    private final UserRepository userRepository;
    private final AccountBookRepository accountBookRepository;

    /**authentication.getName() 으로 해당 user 유뮤 검사 메서드**/
    private User getUser(String email) {
        /*user 찾기*/
        return userRepository.findByEmail(email).orElseThrow(()
                -> new UserException(ErrorCode.EMAIL_NOT_FOUND, ErrorCode.EMAIL_NOT_FOUND.getMessage()));
    }
    /**가계부 id로 해당 AccountBook 유뮤 검사 메서드**/
    private AccountBook getAccountBook(Long id) {
        return accountBookRepository.findById(id).orElseThrow(()
                -> new AccountException(ErrorCode.ACCOUNTBOOK_NOT_FOUND, ErrorCode.ACCOUNTBOOK_NOT_FOUND.getMessage()));
    }

    /**가계부 생성**/
    public AccountAddResponse makeBook(AccountAddDto accountAddDto, String email) {
        User user = getUser(email);
        AccountBook accountBook = accountAddDto.toEntity(user);
        AccountBook savedAccountBook = accountBookRepository.save(accountBook);
        return AccountAddResponse.of(savedAccountBook);
    }

    /**가계부 수정**/
    public AccountModifyResponse updateBook(Long id, AccountModifyDto modifyRequest, String email) {
        /*USER 반환 : User 존재 우무 확인 메서드*/
        User user = getUser(email);
        /*수정 전 엔티티*/
        AccountBook accountBook = getAccountBook(id);
        /*권한 체크 로직 : 자신의 가계부만 수정 가능*/
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION,ErrorCode.INVALID_PERMISSION.getMessage());
        }
        /*수정(변경)지점*/
        AccountBook updatedAccountBook =
                accountBook.update(modifyRequest.getTitle(), modifyRequest.getMemo(), modifyRequest.getBalance());

        return  AccountModifyResponse.of(updatedAccountBook);
    }


    public AccountBookDeleteResponse deleteBook(Long id, String email) {
        /*USER 반환 : User 존재 우무 확인 메서드*/
        User user = getUser(email);
        AccountBook accountBook = getAccountBook(id);
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
        }
        /*🔼검증 로직 통과하면 delete()*/
        accountBookRepository.deleteById(id);
        return AccountBookDeleteResponse.of(id);
    }

    public AccountSelectResponse getBook(Long id, String email) {
        /*USER 반환 : User 존재 우무 확인 메서드*/
        User user = getUser(email);
        AccountBook accountBook = getAccountBook(id);
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
        }
        return AccountSelectResponse.of(accountBook);
    }

    public Page<AccountSelectResponse> getBooks(Pageable pageable, String email) {
        User user = getUser(email);
        Page<AccountBook> accountBooks = accountBookRepository.findByUser(user, pageable);
        return AccountSelectResponse.of(accountBooks);
    }
}
