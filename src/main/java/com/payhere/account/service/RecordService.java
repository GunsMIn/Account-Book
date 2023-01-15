package com.payhere.account.service;

import com.payhere.account.domain.Response.record.*;
import com.payhere.account.domain.dto.record.RecordDto;
import com.payhere.account.domain.dto.record.RecordUpdateDto;
import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.RecordException;
import com.payhere.account.repository.AccountBookRepository;
import com.payhere.account.repository.RecordRepository;
import com.payhere.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecordService {

    private final UserRepository userRepository;
    private final AccountBookRepository accountBookRepository;
    private final RecordRepository recordRepository;
    private final ValidateService validateService;


    /**
     * 가계부 기록 하기
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param recordDto 가계부 기록 add DTO
     * @param email 해당 user 찾는 email param
     *
     * request로 들어오는 act 가 저축일 시 (+) 지출일 시 (-)
     * 가계부 기록 (저축 시 : 잔고+ , 지출 시 : 잔고- )
     *
     * @return RecordResponse 반환
     */
    public RecordResponse spendOrSave(Long bookId, RecordDto recordDto, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);

        Record record = recordDto.toEntity(user, accountBook, recordDto.getAct(), recordDto.getExpendType(), recordDto.getDay());
        /*저축일때 지출일때 나누어서 해당 가계부의 잔고(balance) +,- 로직🔽*/
        if (recordDto.getAct().equals(Act.SAVING.getDescription())) {
            accountBook.addMoney(recordDto.getMoney());
        } else if (recordDto.getAct().equals(Act.SPENDING.getDescription())) {
            accountBook.minusMoney(recordDto.getMoney());
        }else{
            throw new RecordException(ErrorCode.RECORD_FAULT, ErrorCode.RECORD_FAULT.getMessage());
        }
        Record savedRecord = recordRepository.save(record);
        return  RecordResponse.of(savedRecord);
    }


    /**
     * 가계부 기록 수정
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param updateDto 가계부 수정 DTO
     * @param email 해당 user 찾는 email param
     *
     *
     * 가계부기록(Record) 수정 시 가계부(AccountBook) 잔고(balance)도 같이 변화(수정정
     *
     * @return RecordResponse 반환
     */
    public RecordUpdateResponse updateRecord(Long bookId, Long recordId, RecordUpdateDto updateDto, String email) {
        //해당 user 검증 로직 and 해당 AccountBook 검증 로직
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        /*수정 할 레코드 엔티티🔽*/
        Record record = validateService.getRecord(recordId);
        /*수정 변경감지🔽*/
        Record updatedRecord = record.update(record.getMoney(), updateDto ,accountBook );
        return RecordUpdateResponse.of(record);
    }

    /**
     * 가계부 기록 삭제
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param recordId 해당 가계부 기록(record)를 찾기위한 param
     * @param email 사용자의 권한을 체크하기위한 param
     *
     * soft-Delete 사용
     * 기록 삭제 가계부 잔고(balance) 복원
     *
     * @return RecordResponse 반환
     */
    public RecordDeleteResponse deleteRecord(Long bookId, Long recordId, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        Record record = validateService.getRecord(recordId);
        /*Account_Book의 잔고(balacne) 복원 메서드🔽*/
        accountBook.restoreBalance(record.getMoney(), record.getAct());
        /*해당 record 삭제🔽*/
        recordRepository.deleteById(record.getId());
        return RecordDeleteResponse.of(record.getId());
    }

    /**
     * 가계부 기록 복원
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param recordId 해당 가계부 기록(record)를 찾기위한 param
     * @param email 사용자의 권한을 체크하기위한 param
     *
     * soft-Delete에 의해 지원진 record 복원
     * 기록 복원 시 가계부 잔고(balance) 복원
     *
     * @return RecordRestoreResponse 반환
     */
    public RecordRestoreResponse restoreRecord(Long bookId, Long recordId, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        recordRepository.reSave(recordId);
        Record record = validateService.getRecord(recordId);

        accountBook.restoreBalanceResave(record.getMoney(), record.getAct());
        return RecordRestoreResponse.of(recordId);
    }

    /**
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param pageable 해당 가계부 기록(record)를 찾기위한 param
     * @param email 사용자의 권한을 체크하기위한 param
     *
     * soft-Delete 사용
     * 기록 삭제 가계부 잔고(balance) 복원
     *
     * @return RecordResponse 반환
     */
    @Transactional(readOnly = true)
    public Page<RecordListResponse> getRecords(Long bookId, Pageable pageable,String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        Page<Record> records= recordRepository.findByAccountBook(accountBook, pageable);
        return RecordListResponse.of(records);
    }

    /**
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param recordId 해당 가계부 기록(record)를 찾기위한 param
     * @param email 사용자의 권한을 체크하기위한 param
     *
     * 가계부 기록 단건 조회
     *
     * @return RecordSelectResponse 반환
     */
    @Transactional(readOnly = true)
    public RecordSelectResponse getRecord(Long bookId, Long recordId, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        Record record = validateService.getRecord(recordId);
        return RecordSelectResponse.of(record);
    }
}
