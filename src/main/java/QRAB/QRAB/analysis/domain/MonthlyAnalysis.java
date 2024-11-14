package QRAB.QRAB.analysis.domain;

import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthlyAnalysisId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 7)
    private String month; // YYYY-MM 형식

    @Column(nullable = false)
    private int solvedQuizCount;

    @Column(nullable = false)
    private int learningDays;

    @Column(nullable = false)
    private float averageAccuracy;

    public MonthlyAnalysis(User user, String month, int solvedQuizCount, int learningDays, float averageAccuracy) {
        this.user = user;
        this.month = month;
        this.solvedQuizCount = solvedQuizCount;
        this.learningDays = learningDays;
        this.averageAccuracy = averageAccuracy;
    }
}
