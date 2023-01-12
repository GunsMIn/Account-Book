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
    private final ValidateService validateService;

    /**
     * request에 담긴 가계부 정보로 가계부 생성을 진행하는 메서드
     *
     * @param accountAddDto 제목 / 메모 / 잔고
     * @return AccountAddResponse 반환
     */
    public AccountAddResponse makeBook(AccountAddDto accountAddDto, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = accountAddDto.toEntity(user);
        AccountBook savedAccountBook = accountBookRepository.save(accountBook);
        return AccountAddResponse.of(savedAccountBook);
    }

    /**
     * id(가계부id)로 해당 가계부 조회 후
     * request에 담긴 가계부 수정 정보로 가계부 수정을 진행하는 메서드
     *
     * @param modifyRequest 제목 / 메모 / 잔고
     * @return AccountModifyResponse 반환
     */
    public AccountModifyResponse updateBook(Long id, AccountModifyDto modifyRequest, String email) {
        /*USER 반환 : User 존재 우무 확인 메서드*/
        User user = validateService.getUser(email);
        /*수정 전 엔티티*/
        AccountBook accountBook = validateService.getAccountBook(id);
        /*권한 체크 로직 : 자신의 가계부만 수정 가능*/
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION,ErrorCode.INVALID_PERMISSION.getMessage());
        }
        /*수정(변경)지점*/
        AccountBook updatedAccountBook =
                accountBook.update(modifyRequest.getTitle(), modifyRequest.getMemo(), modifyRequest.getBalance());

        return  AccountModifyResponse.of(updatedAccountBook);
    }


    /**
     * id(가계부id)로 해당 가계부 조회 후
     * 가계부 삭제를 진행하는 메서드
     *
     * @return AccountBookDeleteResponse 반환
     */
    public AccountBookDeleteResponse deleteBook(Long id, String email) {
        /*USER 반환 : User 존재 우무 확인 메서드*/
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(id);
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
        }
        /*🔼검증 로직 통과하면 delete()*/
        accountBookRepository.deleteById(id);
        return AccountBookDeleteResponse.of(id);
    }

    /**
     * id(가계부id)로 해당 가계부 단건 조회
     *
     * @return AccountSelectResponse 반환
     */
    @Transactional(readOnly = true)
    public AccountSelectResponse getBook(Long id, String email) {
        /*USER 반환 : User 존재 우무 확인 메서드*/
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(id);
        if (user.getId() != accountBook.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
        }
        return AccountSelectResponse.of(accountBook);
    }

    /**
     * @param pageable 페이징 request
     * @param email 로 해당 user조회 후 해당 user의 AccountBook 페이징 조회
     *
     * @return Page<AccountSelectResponse> 반환
     */
    @Transactional(readOnly = true)
    public Page<AccountSelectResponse> getBooks(Pageable pageable, String email) {
        User user = validateService.getUser(email);
        Page<AccountBook> accountBooks = accountBookRepository.findByUser(user, pageable);
        return AccountSelectResponse.of(accountBooks);
    }
}
