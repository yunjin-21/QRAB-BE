package QRAB.QRAB.profile.dto;

import QRAB.QRAB.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MajorUpdateDTO {
    private String email;

    private Set<String> majorNames; //학과 이름 목록

}
