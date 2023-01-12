package com.payhere.account.service;

import com.payhere.account.config.jwt.JwtUtil;
import com.payhere.account.config.redis.RedisDao;
import com.payhere.account.domain.Response.user.UserLoginResponse;
import com.payhere.account.domain.dto.user.UserJoinDto;
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
    private final ValidateService validateService;
    private final RedisDao redisDao;

    @Value("${jwt.token.secret}")
    private String secretKey;



    /**회원가입**/
    public User join(UserJoinDto userJoinDto) {
        log.info("매개변수로 들어온 값 :{}", userJoinDto);
        /*중복된 이메일인지 check🔽*/
        validateService.checkUserEmail(userJoinDto);
        //비밀번호 암호화
        String encodePassword = encoder.encode(userJoinDto.getPassword());
        log.info(encodePassword);
        User user = userJoinDto.toEntity(encodePassword);
        log.info("user : {}" , user);
        User savedUser = userRepository.save(user);
        log.info("저장된 회원 : {}",savedUser);
        return savedUser;
    }



    /**
     * request에 담긴 회원 정보로 로그인을 진행하는 메서드
     *
     * @param email 로그인 이메일
     * @param password 로그인 비밀번호
     * @return Token이 담겨있는 UserLoginResponse 반환
     */
    //(1.아이디 존재 여부 2.비밀번호 일치 여부) -> 성공 시 토큰 응답
    public UserLoginResponse login(String email,String password) {
        log.info("로그인 아이디 : {} , 비밀번호 : {}" , email,password);
        //1.email 존재 여부 체크
        User user = validateService.getUser(email);
        //2.비밀번호 유효성 검사
        if (!encoder.matches(password, user.getPassword())) {
            throw new UserException(ErrorCode.INVALID_PASSWORD,ErrorCode.INVALID_PASSWORD.getMessage());
        }
        //두 가지 확인중 예외 안났으면 Token발행
        String token = JwtUtil.createJwt(user, secretKey);
        String refreshToken = JwtUtil.createRefreshJwt(user.getEmail(), secretKey);
        redisDao.setValues("RT:" + user.getEmail(), refreshToken);
        return UserLoginResponse.of(token,refreshToken);
    }



    /**UserDetailsService 메서드**/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
