package QRAB.QRAB.bookmark.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkResponseDTO {
    private Long bookmarkId;
    private Long quizId;
    private Long userId;
    private String bookmarkedAt;
}
