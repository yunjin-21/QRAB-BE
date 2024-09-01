package QRAB.QRAB.note.dto;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.note.domain.Note;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //매개변수가 없는 기본 생성자를 자동으로 생성 + 다른 패키지나 클래스에서 직접 생성자를 호출할 수 없지만 상속을 통해 접근 가능
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) //모든 필드를 직렬화하고 역직화함
public class FileRequestDTO {
    private String email;

    @JsonIgnore //특정 필드를 JSON 직렬화 및 역직렬화 과정에서 무시
    private MultipartFile file;
    private Long categoryId; // 카테고리 ID

    public Note toEntity(User user, Category category){
        Note note = Note.builder()
                .file(file != null ? file.getOriginalFilename() : null)
                .user(user)
                .category(category)
                .build();
        return note;
    }
}
