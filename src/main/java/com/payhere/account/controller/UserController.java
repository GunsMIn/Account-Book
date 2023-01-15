package com.payhere.account.controller;

import com.payhere.account.domain.Response.Response;
import com.payhere.account.domain.Response.user.UserJoinResponse;
import com.payhere.account.domain.Response.user.UserLoginResponse;
import com.payhere.account.domain.dto.user.UserJoinDto;
import com.payhere.account.domain.dto.user.UserLoginDto;
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

    @ApiOperation(value = "회원가입", notes = "회원가입 API")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinDto userJoinDto) {
        UserJoinResponse response = userService.join(userJoinDto);
        return Response.success(response);
    }


    @ApiOperation(value = "로그인", notes = "로그인 성공 후 JWT토큰 / Refresh Token 발급")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginDto userLoginDto) {
        UserLoginResponse response = userService.login(userLoginDto.getEmail(), userLoginDto.getPassword());
        return Response.success(response); // 로그인 성공 시 토큰만 반환
    }

}
