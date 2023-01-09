package com.payhere.account.controller;

import com.payhere.account.domain.Response.Response;
import com.payhere.account.domain.Response.UserJoinResponse;
import com.payhere.account.domain.dto.user.UserJoinRequest;
import com.payhere.account.domain.entity.User;
import com.payhere.account.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "User(회원 API)")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    //회원가입 컨트롤러
    @ApiOperation(value = "회원가입", notes = "회원가입 API")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        log.info("userJoinRequest :{} ", userJoinRequest);
        User user = userService.join(userJoinRequest);
        log.info("user :{} ", user);
        UserJoinResponse userJoinResponse = UserJoinResponse.of(user);
        return Response.success(userJoinResponse);
    }

}
