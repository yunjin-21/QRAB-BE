package QRAB.QRAB.bookmark.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookmarkedQuizResponseDTO {
    // 노트 정보
    private Long noteId;
    private String title;
    private List<BookmarkedQuiz> bookmarkedQuizzes;

    //퀴즈 정보
    @Getter
    @Setter
    public static class BookmarkedQuiz {
        private Long quizId;
        private Long quizSetId;
        private String question;
        private List<String> choices;
        private int userAnswer;
        private int correctAnswer;
        private LocalDateTime solvedAt;

        // 생성자
        public BookmarkedQuiz(Long quizId, Long quizSetId, String question, String choicesJson,
                              int userAnswer, int correctAnswer, LocalDateTime solvedAt) {
            this.quizId = quizId;
            this.quizSetId = quizSetId;
            this.question = question;
            this.choices = parseChoices(choicesJson);
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
            this.solvedAt = solvedAt;
        }
    }

    // JSON 문자열을 List<String>으로 변환하는 메소드
    private static List<String> parseChoices(String choicesJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(choicesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse choices JSON", e);
        }
    }
}

