package QRAB.QRAB.bookmark.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BookmarkedQuizResponseDTO {
    private Long quizId;
    private Long quizSetId;
    private String question;
    private List<String> choices;
    private int userAnswer;
    private int correctAnswer;
    private LocalDateTime solvedAt;

    public BookmarkedQuizResponseDTO(Long quizId, Long quizSetId, String question, String choicesJson,
                                     int userAnswer, int correctAnswer, LocalDateTime solvedAt) {
        this.quizId = quizId;
        this.quizSetId = quizSetId;
        this.question = question;
        this.choices = parseChoices(choicesJson); // JSON 문자열을 파싱
        this.userAnswer = userAnswer;
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

