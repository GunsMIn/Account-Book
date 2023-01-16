package com.payhere.account.service;

import com.payhere.account.domain.Response.accountBook.AccountSelectResponse;
import com.payhere.account.domain.dto.accountBook.AccountAddDto;
import com.payhere.account.domain.entity.AccountBook;
import com.payhere.account.domain.entity.User;
import com.payhere.account.fixture.AccountBookFixture;
import com.payhere.account.fixture.AllFixture;
import com.payhere.account.fixture.UserFixture;
import com.payhere.account.repository.AccountBookRepository;
import com.payhere.account.repository.RecordRepository;
import com.payhere.account.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ActiveProfiles("test")
class AccountBookServiceTest {

  /*  AccountBookService accountBookService;

    UserRepository userRepository = mock(UserRepository.class);
    AccountBookRepository accountBookRepository = mock(AccountBookRepository.class);
    RecordRepository recordRepository = mock(RecordRepository.class);
    ValidateService validateService = mock(ValidateService.class);*/

    @InjectMocks
    AccountBookService accountBookService;
    @InjectMocks
    ValidateService validateService;
    @Mock
    UserRepository userRepository;
    @Mock
    AccountBookRepository accountBookRepository;
    @Mock
    RecordRepository recordRepository;

    /*@Test
    @DisplayName("가계부 조회 성공")
    void 가계부_조회_단건() {

        AllFixture all = AllFixture.getDto();
        User user = UserFixture.get(all.getEmail(), all.getPassword());
        AccountBook accountBook = AccountBookFixture.get(user);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountBookRepository.findById(anyLong())).thenReturn(Optional.of(accountBook));
        when(validateService.getUser(any())).thenReturn(user);
        when(validateService.getAccountBook(any())).thenReturn(accountBook);
        AccountSelectResponse response =
                accountBookService.getBook(all.getAccountBookId(), all.getEmail());


        assertEquals(response.getBalance(), all.getBalance());
    }


    @Test
    @DisplayName("가계부 등록 성공")
    void 가계부_등록_성공() {

        AllFixture all = AllFixture.getDto();
        User user = UserFixture.get(all.getEmail(), all.getPassword());
        AccountBook accountBook = AccountBookFixture.get(user);
        AccountAddDto accountAddDto = AccountAddDto.builder()
                .title(accountBook.getTitle())
                .balance(accountBook.getBalance())
                .memo(accountBook.getMemo())
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        accountBookService.makeBook(accountAddDto, user.getEmail());

    }
*/
}