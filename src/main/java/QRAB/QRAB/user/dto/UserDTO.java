package QRAB.QRAB.user.dto;

import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.major.domain.Major;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //해당 클래스와 같은 패키지 or 해당클래스를 상속받은 클래스만 접근 가능
public class UserDTO {
    @NotNull
    @Email
    @Size(max = 50)
    private String username;

    @NotNull
    @Size(min = 2, max = 10)
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]{2,10}$" //닉네임은 특수문자를 제외한 2~10자 이내로 설정
    )
    private String nickname;

    @NotNull
    @Size(min = 10, max = 11)
    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 8, max = 16)  // 길이 제한 추가
    @Pattern(
            regexp = "(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).{8,16}" //비밀번호는 8~16자 길이로, 영문 대문자, 영문 소문자, 숫자, 특수문자를 포함
    )
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 8, max = 16)  // 길이 제한 추가
    private String passwordConfirm;

    private Set<Long> majorIds; // 학과 ID 목록

    private Set<AuthorityDTO> authorityDtoSet;

    public static UserDTO from(User user) { //User 엔티티와 매핑하여 -> UserDTO 로 반환
        if(user == null) return null;
        return UserDTO.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .majorIds(user.getMajors().stream()
                        .map(Major::getId)
                        .collect(Collectors.toSet()))
                .authorityDtoSet(user.getAuthorities().stream()
                        .map(authority -> AuthorityDTO.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
