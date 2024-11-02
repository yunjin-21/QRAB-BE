package QRAB.QRAB.quiz.domain;

import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class QuizSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizSetId;

    private int totalQuestions;
    private LocalDateTime createdAt;
    private String status;
    private float accuracyRate = 0.0f; // 초기 정답률

    @OneToMany(mappedBy = "quizSet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Quiz> quizzes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", referencedColumnName = "note_id") // Note와의 관계 설정
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id") // User와의 관계 설정
    private User user;

    public QuizSet() {
        // 기본 생성자
    }

    public Long getQuizSetId() {
        return quizSetId;
    }

    public void setQuizSetId(Long quizSetId) {
        this.quizSetId = quizSetId;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }
}
