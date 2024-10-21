package QRAB.QRAB.quiz.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    private String difficulty;
    private String question;
    private String choices; // JSON 문자열로 저장
    private int correctAnswer;
    private String explanation;
    private String quizSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id")
    private QuizSet quizSet;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    // Choices를 리스트로 변환
    public List<String> getChoicesAsList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(choices, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse choices to List", e);
        }
    }

    // Choices를 JSON 문자열로 설정
    public void setChoicesAsList(List<String> choicesList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.choices = mapper.writeValueAsString(choicesList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert choices List to JSON", e);
        }
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getQuizSummary() {
        return quizSummary;
    }

    public void setQuizSummary(String quizSummary) {
        this.quizSummary = quizSummary;
    }

    public QuizSet getQuizSet() {
        return quizSet;
    }

    public void setQuizSet(QuizSet quizSet) {
        this.quizSet = quizSet;
    }
}
