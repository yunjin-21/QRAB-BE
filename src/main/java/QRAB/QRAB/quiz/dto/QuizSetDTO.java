package QRAB.QRAB.quiz.dto;

import QRAB.QRAB.quiz.domain.QuizSet;

import java.time.LocalDateTime;

public class QuizSetDTO {
    private Long quizSetId;
    private Long noteId;
    private Long userId;
    private int totalQuestions;
    private LocalDateTime createdAt;
    private String status;
    private float accuracyRate;

    public QuizSetDTO(QuizSet quizSet){
        this.quizSetId = quizSet.getQuizSetId();
        this.noteId = quizSet.getNote() != null ? quizSet.getNote().getId() : null;
        this.userId = quizSet.getUser() != null ? quizSet.getUser().getUserId() : null; // User 객체에서 ID 가져오기
        this.totalQuestions = quizSet.getTotalQuestions();
        this.createdAt = quizSet.getCreatedAt();
        this.status = quizSet.getStatus();
        this.accuracyRate = quizSet.getAccuracyRate();
    }

    public QuizSetDTO(Long quizSetId, Long noteId, Long userId, int totalQuestions,
                      LocalDateTime createdAt, String status, float accuracyRate) {
        this.quizSetId = quizSetId;
        this.noteId = noteId;
        this.userId = userId;
        this.totalQuestions = totalQuestions;
        this.createdAt = createdAt;
        this.status = status;
        this.accuracyRate = accuracyRate;
    }

    public Long getQuizSetId() {
        return quizSetId;
    }

    public void setQuizSetId(Long quizSetId) {
        this.quizSetId = quizSetId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(float accuracyRate) {
        this.accuracyRate = accuracyRate;
    }
}
