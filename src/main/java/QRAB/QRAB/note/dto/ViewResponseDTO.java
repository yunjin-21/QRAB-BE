package QRAB.QRAB.note.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ViewResponseDTO {
    private Long noteId;
    private int restrictedAccess; // 공개 상태 (0: 공개, 1: 비공개)
}
