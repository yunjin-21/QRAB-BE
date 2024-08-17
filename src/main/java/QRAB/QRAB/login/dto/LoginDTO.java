package QRAB.QRAB.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotNull
    @Email
    @Size(max = 50)
    private String username;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;
}