package QRAB.QRAB.user.controller;


import QRAB.QRAB.user.auth.JwtFilter;
import QRAB.QRAB.user.auth.TokenProvider;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.dto.LoginDTO;
import QRAB.QRAB.user.dto.TokenDTO;
import QRAB.QRAB.user.service.UserService;
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
@RequestMapping("/users")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserService userService;
    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
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

        Object principal = authentication.getPrincipal();
        // principal이 org.springframework.security.core.userdetails.User 인스턴스일 경우 처리
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            // Spring Security의 User 객체를 사용하여 User 정보를 직접 사용
            org.springframework.security.core.userdetails.User springSecurityUser = (org.springframework.security.core.userdetails.User) principal;

            // 필요한 경우 User의 username을 사용하여 애플리케이션의 User 객체를 사용
            String username = springSecurityUser.getUsername();

            // 로그인 기록 저장 (사용자 정보만 필요하면 username을 활용 가능)
            userService.logDailyLogin(username);  // Username으로 로그인 기록 저장 호출
        }

        return new ResponseEntity<>(new TokenDTO(jwt), httpHeaders, HttpStatus.OK); // TokenDto를 이용해 responsebody에도 토큰 넣어줌
    }
}
