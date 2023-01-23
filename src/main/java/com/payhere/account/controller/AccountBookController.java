package com.payhere.account.controller;

import com.payhere.account.domain.Response.Response;
import com.payhere.account.domain.Response.accountBook.AccountAddResponse;
import com.payhere.account.domain.Response.accountBook.AccountBookDeleteResponse;
import com.payhere.account.domain.Response.accountBook.AccountModifyResponse;
import com.payhere.account.domain.Response.accountBook.AccountSelectResponse;
import com.payhere.account.domain.dto.accountBook.AccountAddDto;
import com.payhere.account.domain.dto.accountBook.AccountModifyDto;
import com.payhere.account.service.AccountBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "AccountBook(가계부 API)")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/account_books")
public class AccountBookController {

    private final AccountBookService accountBookService;

    @ApiOperation(value ="가계부 생성 API", notes = "balance : 잔고 , title : 가계부 제목 , memo : 가계부 머릿말 메모 등록 후 가계부 생성 API")
    @PostMapping
    public Response makeAccountBook(@Validated @RequestBody AccountAddDto addRequest, @ApiIgnore Authentication authentication) {
        AccountAddResponse response = accountBookService.makeBook(addRequest, authentication.getName());
        return Response.success(response);
    }

    @ApiOperation(value ="가계부 수정 API", notes = "해당 가계부 수정 API(잔고,가계부 제목,가계부 머릿말 메모 수정)")
    @PatchMapping("/{id}")
    public Response modifyAccountBook(@PathVariable Long id, @Validated @RequestBody AccountModifyDto modifyRequest, @ApiIgnore Authentication authentication) {
        AccountModifyResponse response = accountBookService.updateBook(id, modifyRequest, authentication.getName());
        return Response.success(response);
    }

    @ApiOperation(value ="가계부 삭제 API", notes = "해당 가계부 삭제 API")
    @DeleteMapping("/{id}")
    public Response removeAccountBook(@PathVariable Long id, @ApiIgnore Authentication authentication) {
        AccountBookDeleteResponse response = accountBookService.deleteBook(id, authentication.getName());
        return Response.success(response);
    }

    @ApiOperation(value ="가계부 단건 조회 API", notes = "@PathVariable id값으로 해당 가계부 조회")
    @GetMapping("/{id}")
    public Response<AccountSelectResponse> getOneAccountBook(@PathVariable Long id, @ApiIgnore Authentication authentication) {
        AccountSelectResponse response = accountBookService.getBook(id, authentication.getName());
        return Response.success(response);
    }

    @ApiOperation(value = "가게부 페이징 조회(최신순)", notes = "가장 최신에 작성한 가게부 5개 페이징 API")
    @GetMapping
    public Response getListAccountBook(@PageableDefault(size = 5, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable,@ApiIgnore Authentication authentication) {
        Page<AccountSelectResponse> response = accountBookService.getBooks(pageable,authentication.getName());
        return Response.success(response);
    }


}
