package com.payhere.account.service;

import com.payhere.account.domain.Response.record.*;
import com.payhere.account.domain.dto.record.RecordDto;
import com.payhere.account.domain.dto.record.RecordUpdateDto;
import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.Act;
import com.payhere.account.exception.AccountException;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.RecordException;
import com.payhere.account.exception.UserException;
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
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param recordDto 가계부 기록 add DTO
     * @param email 해당 user 찾는 email param
     *
     * request로 들어오는 act 가 저축일 시 (+) 지출일 시 (-)
     * 가계부 기록 (저축 시 : 잔고+ , 지출 시 : 잔고- )
     *
     * @return RecordResponse 반환
     */
    public RecordResponse addOrMinus(Long bookId, RecordDto recordDto, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);

        Record record = recordDto.toEntity(user, accountBook, recordDto.getAct(), recordDto.getExpendType(), recordDto.getDay());
        if (recordDto.getAct().equals(Act.SAVING.getDescription())) {
            log.info("저축메소드");
            accountBook.addMoney(recordDto.getMoney());
        } else if (recordDto.getAct().equals(Act.SPENDING.getDescription())) {
            log.info("지출메소드");
            accountBook.minusMoney(recordDto.getMoney());
        }else{
            throw new RecordException(ErrorCode.RECORD_FAULT, ErrorCode.RECORD_FAULT.getMessage());
        }
        Record savedRecord = recordRepository.save(record);
        return  RecordResponse.of(savedRecord);
    }


    /**
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param recordDto 가계부 기록 add DTO
     * @param email 해당 user 찾는 email param
     *
     *
     * 가계부기록(Record) 수정 시 가계부(AccountBook) 잔고(balance)도 같이 변화(수정정
     *
     * @return RecordResponse 반환
     */
    /**가계부 기록 수정**/
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
     * @param bookId 가계부 id로 해당 가계부 조회 param
     * @param recordId 해당 가계부 기록(record)를 찾기위한 param
     * @param email 사용자의 권한을 체크하기위한 param
     *
     * soft-Delete 사용
     * 기록 삭제 가계부 잔고(balance) 복원
     *
     * @return RecordResponse 반환
     */
    /**가계부 기록 삭제 및 복원**/
    public RecordDeleteResponse deleteRecord(Long bookId, Long recordId, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        Record record = validateService.getRecord(recordId);
        /*Account_Book의 잔고(balacne) 복원 메서드🔽*/
        accountBook.restore(record.getMoney(), record.getAct());
        /*해당 record 삭제🔽*/
        recordRepository.deleteById(record.getId());
        return RecordDeleteResponse.of(record.getId());
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
    /**가계부 기록 삭제 및 복원**/
    @Transactional(readOnly = true)
    public Page<RecordListResponse> getRecords(Long bookId, Pageable pageable,String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        Page<Record> records= recordRepository.findByAccountBook(accountBook, pageable);
        return RecordListResponse.of(records);
    }

    @Transactional(readOnly = true)
    public RecordSelectResponse getRecord(Long bookId, Long recordId, String email) {
        User user = validateService.getUser(email);
        AccountBook accountBook = validateService.getAccountBook(bookId);
        validateService.checkAuthority(user, accountBook);
        Record record = validateService.getRecord(recordId);
        return RecordSelectResponse.of(record);
    }
}
