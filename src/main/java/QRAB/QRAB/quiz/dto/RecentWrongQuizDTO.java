package QRAB.QRAB.quiz.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecentWrongQuizDTO {
    private Long quizId;
    private String question;
    private List<String> choices;
    private int selectedAnswer;
    private int correctAnswer;
    private LocalDateTime solvedAt;

    public RecentWrongQuizDTO(Long quizId, String question, String choicesJson,
                              int selectedAnswer, int correctAnswer, LocalDateTime solvedAt) {
        this.quizId = quizId;
        this.question = question;
        this.choices = parseChoices(choicesJson);
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.solvedAt = solvedAt;
    }

    // JSON 문자열을 List<String>으로 변환
    private List<String> parseChoices(String choicesJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(choicesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse choices JSON", e);
        }
    }
}

