package com.payhere.account.config.security;
import com.payhere.account.config.jwt.JwtFilter;
import com.payhere.account.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    @Value("${jwt.token.secret}") // yml에 저장된 값을 가져온다.
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .cors()
                .and()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/users/join", "/api/users/login", "/swagger-ui").permitAll() // join, login은 언제나 가능
                .antMatchers(HttpMethod.GET, "/api/**").authenticated()   // 모든 get 요청 허용
                .antMatchers(HttpMethod.POST, "/api/**").authenticated()  // 순서대로 적용이 되기 때문에 join, login 다음에 써주기
                .antMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                //UserNamePasswordAuthenticationFilter 적용하기 전에 JWTTokenFilter를 적용 하라는 뜻.
                .build();
    }

}