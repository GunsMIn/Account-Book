package com.payhere.account.service;

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

import java.util.List;
import java.util.Optional;

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

        /*중복된 이메일인지 check*/
        userRepository.findByEmail(userJoinRequest.getEmail())
            .ifPresent(user -> {
                throw new UserException(ErrorCode.DUPLICATED_USER_NAME);
            });

        //암호화 된 password db save
        String encodePassword = encoder.encode(userJoinRequest.getPassword());
        User user = userJoinRequest.toEntity(encodePassword);
        User savedUser = userRepository.save(user);
        log.info("저장된 회원 : {}",savedUser);
        return savedUser;
    }


    /**UserDetailsService 메서드**/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
