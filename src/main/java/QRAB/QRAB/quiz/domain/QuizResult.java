package QRAB.QRAB.quiz.domain;

import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id", nullable = false)
    private QuizSet quizSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int score;
    private int correctCount;
    private int totalQuestions;
    private LocalDateTime takenAt;

    public QuizResult() {
    }

    public QuizResult(QuizSet quizSet, User user, int score, int correctCount, int totalQuestions, LocalDateTime takenAt) {
        this.quizSet = quizSet;
        this.user = user;
        this.score = score;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.takenAt = takenAt;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public QuizSet getQuizSet() {
        return quizSet;
    }

    public void setQuizSet(QuizSet quizSet) {
        this.quizSet = quizSet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(LocalDateTime takenAt) {
        this.takenAt = takenAt;
    }
}
