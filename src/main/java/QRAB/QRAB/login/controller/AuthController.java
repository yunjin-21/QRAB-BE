package QRAB.QRAB.login.controller;


import QRAB.QRAB.login.auth.JwtFilter;
import QRAB.QRAB.login.auth.TokenProvider;
import QRAB.QRAB.login.dto.LoginDTO;
import QRAB.QRAB.login.dto.TokenDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //반환된 객체는 JSON 형식으로 클라이언트에 전송
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    //login
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDTO> authorize(@Valid @RequestBody LoginDTO loginDto) {

        // UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());//LoginDto의 아이디(email), password로 인증토큰을 생성

        // AuthenticationManager을 통해 인증 수행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);//토큰으로 UserDetailsService의 loadUserByUsername 메소드가 실행됨
        //인증 정보를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 인증정보(authentication)를 가지고 jwt 토큰 생성
        String jwt = tokenProvider.createToken(authentication);
        // JWT를 헤더에 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt); // 헤더에 토큰 넣음(Bearer 필수)

        return new ResponseEntity<>(new TokenDTO(jwt), httpHeaders, HttpStatus.OK); // TokenDto를 이용해 responsebody에도 토큰 넣어줌
    }
}
