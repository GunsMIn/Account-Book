package com.payhere.account.controller;

import com.payhere.account.domain.Response.Response;
import com.payhere.account.domain.Response.record.*;
import com.payhere.account.domain.dto.record.RecordDto;
import com.payhere.account.domain.dto.record.RecordUpdateDto;
import com.payhere.account.domain.entity.Record;
import com.payhere.account.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "Record(가계부 기록 API)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account_books")
public class RecordController {

    private final RecordService recordService;

    @ApiOperation("가계부 기록 쓰기 지출/저축 API")
    @PostMapping("/{bookId}/records")
    public Response saveOrSpend(@PathVariable Long bookId, @RequestBody RecordDto recordDto, @ApiIgnore Authentication authentication) {
        RecordResponse response = recordService.spendOrSave(bookId, recordDto, authentication.getName());
        return Response.success(response);
    }

    @ApiOperation("가계부 기록 수정 API")
    @PatchMapping("/{bookId}/records/{recordId}")
    public Response modifyRecord(@PathVariable Long bookId, @PathVariable Long recordId , @RequestBody RecordUpdateDto recordDto, @ApiIgnore Authentication authentication) {
        RecordUpdateResponse response = recordService.updateRecord(bookId, recordId, recordDto, authentication.getName());
        return Response.success(response);
    }

    @ApiOperation("가계부 기록 삭제 API")
    @DeleteMapping("/{bookId}/records/{recordId}")
    public Response removeRecord(@PathVariable Long bookId, @PathVariable Long recordId, @ApiIgnore Authentication authentication) {
        RecordDeleteResponse response = recordService.deleteRecord(bookId, recordId,authentication.getName());
        return Response.success(response);
    }

    @ApiOperation(value = "가계부 기록 페이징 리스트 API", notes = "가장 최신에 작성한 가게부 기록 20개 페이징 API")
    @GetMapping("/{bookId}/records")
    public Response getListRecord(@PathVariable Long bookId, @ApiIgnore Authentication authentication,@PageableDefault(size = 20, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RecordListResponse> responses = recordService.getRecords(bookId, pageable, authentication.getName());
        return Response.success(responses);
    }

    @ApiOperation("가계부 기록 단건 조회 API")
    @GetMapping("/{bookId}/records/{recordId}")
    public Response getOneRecord(@PathVariable Long bookId,@PathVariable Long recordId ,@ApiIgnore Authentication authentication) {
        RecordSelectResponse response = recordService.getRecord(bookId, recordId, authentication.getName());
        return Response.success(response);
    }

    @ApiOperation("가계부 기록 복원 API")
    @PostMapping("/{bookId}/records/{recordId}/restore")
    public Response restoreRecord(@PathVariable Long bookId,@PathVariable Long recordId ,@ApiIgnore Authentication authentication) {
        RecordRestoreResponse response = recordService.restoreRecord(bookId, recordId, authentication.getName());
        return Response.success(response);
    }

}
