package com.payhere.account.controller;

import com.payhere.account.domain.Response.Response;
import com.payhere.account.domain.Response.Result;
import com.payhere.account.domain.Response.accountBook.AccountAddResponse;
import com.payhere.account.domain.Response.accountBook.AccountModifyResponse;
import com.payhere.account.domain.dto.accountBook.AccountAddRequest;
import com.payhere.account.domain.dto.accountBook.AccountModifyRequest;
import com.payhere.account.service.AccountBookService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "AccountBook(가계부 API)")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountBookController {

    private final AccountBookService accountBookService;


    /*
    * {
  "balance": 1000000,
  "memo": "2023년 가계부",
  "title": "거누의 가계부"
}
    * */
    @PostMapping("/api/accountBook")
    public Response<AccountAddResponse> makeAccountBook(@RequestBody AccountAddRequest addRequest, @ApiIgnore Authentication authentication) {
        log.info("가계부 등록 인증인 email : {}" , authentication.getName());
        AccountAddResponse response = accountBookService.makeBook(addRequest, authentication.getName());
        return Response.success(response);
    }

    @PatchMapping("/api/accountBook/{id}")
    public Response<?> modifyAccountBook(@PathVariable Long id, @RequestBody AccountModifyRequest modifyRequest, @ApiIgnore Authentication authentication) {
        log.info("가계부 수정 인증인 email : {}" , authentication.getName());
        AccountModifyResponse response = accountBookService.updateBook(id, modifyRequest, authentication.getName());
        return Response.success(response);
    }




}
