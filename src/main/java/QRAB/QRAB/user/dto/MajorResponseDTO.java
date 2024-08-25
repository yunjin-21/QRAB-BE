package QRAB.QRAB.user.dto;

import QRAB.QRAB.major.domain.Major;
import lombok.*;

@Getter
@AllArgsConstructor
public class MajorResponseDTO {
    private Long id;
    private String department;
    private String name;
    public static MajorResponseDTO fromEntity(Major major){
        return new MajorResponseDTO(major.getId(), major.getDepartment(), major.getName());
    }
}
