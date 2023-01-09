package com.payhere.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


@RestController
public class PayHereController {

    @GetMapping(value = "/api/hello", produces = "application/json; charset=utf8")
    public String hello() {
        String value = "안녕하세요 PayHere 지원자 김건우입니다";
        return value;
    }

}
