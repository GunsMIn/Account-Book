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

    /*   {
  "email": "medneter@naver.com",
  "password": "string",

}  */

    //회원가입 컨트롤러
    @ApiOperation(value = "회원가입", notes = "회원가입 API")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinDto userJoinDto) {
        log.info("userJoinRequest :{} ", userJoinDto);
        User user = userService.join(userJoinDto);
        log.info("user :{} ", user);
        UserJoinResponse userJoinResponse = UserJoinResponse.of(user);
        return Response.success(userJoinResponse);
    }

    //로그인 컨트롤러
    @ApiOperation(value = "로그인", notes = "로그인 성공 후 JWT토큰 발급")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginDto userLoginDto) {
        log.info("loginEmail : {}", userLoginDto.getEmail());
        String token = userService.login(userLoginDto.getEmail(), userLoginDto.getPassword());
        return Response.success(new UserLoginResponse(token)); // 로그인 성공 시 토큰만 반환
    }

}
