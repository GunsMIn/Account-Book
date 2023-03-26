package com.payhere.account.service;

import com.payhere.account.config.jwt.JwtUtil;
import com.payhere.account.config.redis.RedisDao;
import com.payhere.account.domain.Response.user.UserAdminResponse;
import com.payhere.account.domain.Response.user.UserDeleteResponse;
import com.payhere.account.domain.Response.user.UserJoinResponse;
import com.payhere.account.domain.Response.user.UserLoginResponse;
import com.payhere.account.domain.dto.user.UserJoinDto;
import com.payhere.account.domain.dto.user.UserRoleDto;
import com.payhere.account.domain.entity.User;
import com.payhere.account.domain.entity.type.UserRole;
import com.payhere.account.exception.ErrorCode;
import com.payhere.account.exception.customException.UserException;
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

    /**
     * request에 담긴 회원 정보로 로그인을 진행하는 메서드
     *
     * @param userJoinDto 회원의 이름 , 이메일 , 비밀번호 저장
     *                    <p>
     *                    BCryptPasswordEncoder로 비밀번호 암호화 후 DB저장
     * @return UserJoinResponse 반환
     */
    public UserJoinResponse join(UserJoinDto userJoinDto) {
        /*중복된 이메일인지 check🔽*/
        validateService.checkUserEmail(userJoinDto);
        //비밀번호 암호화
        String encodePassword = encoder.encode(userJoinDto.getPassword());
        log.info(encodePassword);
        User user = userJoinDto.toEntity(encodePassword);
        User savedUser = userRepository.save(user);
        return UserJoinResponse.of(savedUser);
    }


    /**
     * request에 담긴 회원 정보로 로그인을 진행하는 메서드
     *
     * @param email    로그인 이메일
     * @param password 로그인 비밀번호
     * @return JWT, refreshToken 이 담겨있는 UserLoginResponse 반환
     */
    //(1.아이디 존재 여부 2.비밀번호 일치 여부) -> 성공 시 토큰 응답
    public UserLoginResponse login(String email, String password) {
        log.info("로그인 아이디 : {} , 비밀번호 : {}", email, password);
        //1.email 존재 여부 체크
        User user = validateService.getUser(email);
        //2.비밀번호 유효성 검사
        if (!encoder.matches(password, user.getPassword())) {
            throw new UserException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
        }
        //두 가지 확인중 예외 안났으면 Token발행
        String token = JwtUtil.createJwt(user, secretKey);
        String refreshToken = JwtUtil.createRefreshJwt(user.getEmail(), secretKey);
        redisDao.setValues("RT:" + user.getEmail(), refreshToken);
        return UserLoginResponse.of(token, refreshToken);
    }

    /**
     * 회원의 role이 ADMIN(관리자) 회원만 사용자의 권한을 바꿀 수 있는 서비스 로직
     *
     * @param email       로그인 이메일
     * @param id          권한 수정 대상 회원의 id
     * @param userRoleDto 권한 변경 값(USER.ADMIN)
     * @return UserAdminResponse 반환
     */
    public UserAdminResponse changeRole(String email, Long id, UserRoleDto userRoleDto) {
        //회원 검증 + UserRole 검증 메서드
        User user = checkUserRole(email, id, userRoleDto);
        UserAdminResponse userAdminResponse = UserAdminResponse.of(user);
        return userAdminResponse;
    }

    /**
     * 1.해당 회원이 ADMIN인지 검사 / 2.{ID} 바뀔 대상 조회 / 3.RequsetBody의 값 검사
     **/
    private User checkUserRole(String email, Long id, UserRoleDto userRoleDto) {
        //주의! findUser와 changedUser 변수 혼동 No
        //findUser는 토큰을 통해 인증 된 회원 -> 로그인된 회원
        User findUser = validateService.getUser(email);
        //Admin회원만 UserRole 전환 가능
        if (findUser.getRole().equals(UserRole.USER)) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, "관리자(ADMIN)만 권한 변경을 할 수 있습니다.");
        }
        //@PathVariable로 들어온 id로 조회 -> role 변환 될 대상
        User changedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, String.format("%d번 회원은 존재하지 않습니다", id)));
        // requestBody에 들어올 값과 UserRole 비교
        if (userRoleDto.getRole().toUpperCase().equals(UserRole.USER.name())) {
            changedUser.changeRole(UserRole.USER);
        } else if (userRoleDto.getRole().toUpperCase().equals(UserRole.ADMIN.name())) {
            changedUser.changeRole(UserRole.ADMIN);
        } else {
            throw new UserException(ErrorCode.USER_ROLE_NOT_FOUND, ErrorCode.USER_ROLE_NOT_FOUND.getMessage());
        }

        return changedUser;
    }


    /**
     * 회원 삭제 메서드
     *
     * @param id    삭제 될 회원의 id
     * @param email 로그인 email
     * @return UserDeleteResponse 반환
     */
    public UserDeleteResponse removeUser(Long id, String email) {
        User user = validateService.getUser(email);
        User findUser = userRepository.findById(id).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        /*삭제 권한 없음🔽*/
        if (user.getId() != findUser.getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION);
        }
        /*삭제 진행*/
        userRepository.deleteById(id);
        return UserDeleteResponse.of(id);
    }


    /**
     * UserDetailsService 메서드
     **/
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
    }


}
