package QRAB.QRAB.quiz.dto;

import QRAB.QRAB.quiz.domain.QuizResult;
import QRAB.QRAB.quiz.domain.QuizSet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizResultDTO {
    private Long quizSetId;
    private String noteTitle;
    private int totalQuestions;
    private String createdAt;
    private String solvedAt;
    private String answerSummary;
    private String categoryName;
    private String parentCategoryName;

    public QuizResultDTO(String noteTitle, int totalQuestions, String createdDate, String solvedDate, String answerSummary, String categoryName, String parentCategoryName) {
        this.noteTitle = noteTitle;
        this.totalQuestions = totalQuestions;
        this.createdAt = createdAt;
        this.solvedAt = solvedAt;
        this.answerSummary = answerSummary;
        this.categoryName = categoryName;
        this.parentCategoryName = parentCategoryName;
    }

    public QuizResultDTO(QuizSet quizSet, QuizResult quizResult) {
        this.quizSetId = quizSet.getQuizSetId();
        this.noteTitle = quizSet.getNote().getTitle();
        this.totalQuestions = quizSet.getTotalQuestions();
        this.createdAt = quizSet.getCreatedAt().toString();
        this.solvedAt = quizResult.getTakenAt() != null ? quizResult.getTakenAt().toString() : null;
        this.answerSummary = generateAnswerSummary(quizResult); // 정답/오답 요약 생성
        this.categoryName = quizSet.getNote().getCategory().getName();
        this.parentCategoryName = quizSet.getNote().getCategory().getParentCategory() != null
                ? quizSet.getNote().getCategory().getParentCategory().getName()
                : "";
    }

    private String generateAnswerSummary(QuizResult quizResult) {
        int correctCount = quizResult.getCorrectCount();
        int totalQuestions = quizResult.getTotalQuestions();
        int wrongCount = totalQuestions - correctCount;

        return correctCount > wrongCount
                ? "정답 " + correctCount + "문제"
                : "오답 " + wrongCount + "문제";
    }
}
