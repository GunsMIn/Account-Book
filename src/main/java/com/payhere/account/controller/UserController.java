package com.payhere.account.controller;

import com.payhere.account.domain.Response.Response;
import com.payhere.account.domain.Response.user.UserAdminResponse;
import com.payhere.account.domain.Response.user.UserJoinResponse;
import com.payhere.account.domain.Response.user.UserLoginResponse;
import com.payhere.account.domain.dto.user.UserJoinDto;
import com.payhere.account.domain.dto.user.UserLoginDto;
import com.payhere.account.domain.dto.user.UserRoleDto;
import com.payhere.account.domain.entity.User;
import com.payhere.account.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "User(회원 API)")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "회원가입", notes = "email , password , name 으로 회원가입 진행 API")
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


    @ApiOperation(value = "회원 UserRole(USER -> ADMIN, ADMIN -> USER) 전환", notes = "ADMIN 회원만이 일반 회원을 ADMIN으로 승격시키는 API(일반회원 등급업 기능 불가) ")
    @PostMapping("/{id}/role_change")
    public Response<UserAdminResponse> updateUserRole(@PathVariable Long id, @RequestBody UserRoleDto userRoleDto, @ApiIgnore Authentication authentication) {
        UserAdminResponse changeRoleResponse = userService.changeRole(authentication.getName(), id, userRoleDto);
        return Response.success(changeRoleResponse);
    }
}
