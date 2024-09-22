package QRAB.QRAB.profile.dto;

import QRAB.QRAB.profile.domain.Profile;
import QRAB.QRAB.user.domain.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) //모든 필드를 직렬화하고 역직화함
public class ProfileUpdateDTO {
    @NotNull
    @Email
    @Size(max = 50)
    private String email; //이메일 수정 가능? no
    @NotNull
    @Size(min = 2, max = 10)
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]{2,10}$" //닉네임은 특수문자를 제외한 2~10자 이내로 설정
    )
    private String nickName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 8, max = 16)  // 길이 제한 추가
    @Pattern(
            regexp = "(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).{8,16}" //비밀번호는 8~16자 길이로, 영문 대문자, 영문 소문자, 숫자, 특수문자를 포함
    )
    private String password;
    @NotNull
    @Size(min = 10, max = 11)
    private String phoneNumber;

    @JsonIgnore
    private MultipartFile imgUrl;


    /*public Profile toEntity(User user){ //when you are converting a DTO into an entity,
        Profile profile = Profile.builder()
                .user(user)
                .nickname(nickname)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .build();
        return profile;
    }*/
}
