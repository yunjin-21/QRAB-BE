package QRAB.QRAB.quiz.domain;

import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
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

    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizAnswer> answers;
}
