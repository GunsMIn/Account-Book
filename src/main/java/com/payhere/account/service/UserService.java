package com.payhere.account.service;

import com.payhere.account.config.jwt.JwtUtil;
import com.payhere.account.domain.dto.user.UserJoinRequest;
import com.payhere.account.domain.entity.User;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.UserException;
import com.payhere.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60; //1시간

    /**회원가입**/
    public User join(UserJoinRequest userJoinRequest) {
        log.info("매개변수로 들어온 값 :{}", userJoinRequest);
        /*중복된 이메일인지 check*/
        userRepository.findByEmail(userJoinRequest.getEmail())
            .ifPresent(user -> {
                throw new UserException(ErrorCode.DUPLICATED_EMAIL,ErrorCode.DUPLICATED_EMAIL.getMessage());
            });

        //비밀번호 암호화
        String encodePassword = encoder.encode(userJoinRequest.getPassword());
        log.info(encodePassword);
        User user = userJoinRequest.toEntity(encodePassword);
        log.info("user : {}" , user);
        User savedUser = userRepository.save(user);
        log.info("저장된 회원 : {}",savedUser);
        return savedUser;
    }

    /**로그인**/
    //(1.아이디 존재 여부 2.비밀번호 일치 여부) -> 성공 시 토큰 응답
    public String login(String email,String password) {
        log.info("로그인 아이디 : {} , 비밀번호 : {}" , email,password);
        //1.아이디 존재 여부 체크
        User user = checkUser(email);
        //2.비밀번호 유효성 검사
        if (!encoder.matches(password, user.getPassword())) {
            throw new UserException(ErrorCode.INVALID_PASSWORD,ErrorCode.INVALID_PASSWORD.getMessage());
        }
        //두 가지 확인중 예외 안났으면 Token발행
        String token = JwtUtil.generateToken(email, secretKey, expireTimeMs);
        return token;
    }


    /**authentication.getName() 으로 해당 user 유뮤 검사 메서드**/
    private User checkUser(String email) {
        /*user 찾기*/
        return userRepository.findByEmail(email).orElseThrow(()
                -> new UserException(ErrorCode.EMAIL_NOT_FOUND, ErrorCode.EMAIL_NOT_FOUND.getMessage()));
    }

    /**UserDetailsService 메서드**/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
