package QRAB.QRAB.login.service;


import QRAB.QRAB.login.domain.Authority;
import QRAB.QRAB.login.domain.User;
import QRAB.QRAB.login.dto.UserDTO;
import QRAB.QRAB.login.excepiton.DuplicateMemberException;
import QRAB.QRAB.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO signup(UserDTO userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 존재하는 이메일입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER") //기본 유저는 ROLE_USER 권한만 가지고 있음
                .build();
        // userDTO 로부터 User 엔티티 생성
        User user = User.builder()
                .username(userDto.getUsername())
                .realName(userDto.getRealName())
                .nickname(userDto.getNickname())
                .phoneNumber(userDto.getPhoneNumber())
                .password(passwordEncoder.encode(userDto.getPassword())) //비밀번호 암호화
                .authorities(Collections.singleton(authority))
                .activated(true) //활성화된 상태로 설정
                .build();


        User savedUser = userRepository.save(user);
        return UserDTO.from(savedUser); //api 응답의 일관성유지와 User 엔티티 보호를 위해 UserDto 로 변환해서 반환
    }
}