package QRAB.QRAB.login.dto;

import QRAB.QRAB.login.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
    @Size(max = 50)
    private String realName;

    @NotNull
    @Size(min = 3, max = 50)
    private String nickname;

    @NotNull
    @Size(min = 10, max = 11)
    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 6, max = 100)
    private String passwordConfirm;

    private Set<AuthorityDTO> authorityDtoSet;

    public static UserDTO from(User user) { //User 엔티티와 매핑하여 -> UserDTO 로 반환
        if(user == null) return null;
        return UserDTO.builder()
                .username(user.getUsername())
                .realName(user.getRealName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .authorityDtoSet(user.getAuthorities().stream()
                        .map(authority -> AuthorityDTO.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
