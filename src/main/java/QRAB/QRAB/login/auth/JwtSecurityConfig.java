package QRAB.QRAB.login.auth;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;

    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // jwt 토큰이 먼저 인증되고(JwtFilter의 doFilter 메서드 실행), 그 후 사용자 인증(Spring Security 기본 제공 클래스) 실행
    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                new JwtFilter(tokenProvider), //토큰 추출 후  유효성 검증
                UsernamePasswordAuthenticationFilter.class //인증 절차 요청 처리 수행
        );

    }
}
