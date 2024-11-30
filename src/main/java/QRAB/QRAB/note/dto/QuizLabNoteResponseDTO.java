package QRAB.QRAB.note.dto;

import QRAB.QRAB.note.domain.Note;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizLabNoteResponseDTO {
    private Long noteId;
    private String title;
    private String chatgptContent;
    private String categoryName;
    private String parentCategoryName;
    private String fileOrUrl;

    @JsonProperty("isSolved")
    private boolean isSolved;

    @JsonIgnore
    public boolean getSolved() {
        return isSolved;
    }
}

