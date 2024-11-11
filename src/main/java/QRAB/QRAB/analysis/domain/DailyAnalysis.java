package QRAB.QRAB.analysis.domain;

import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DailyAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyAnalysisId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int solvedQuizCount;

    @Column(nullable = false)
    private float averageAccuracy;

    public DailyAnalysis(User user, LocalDate date, int solvedQuizCount, float averageAccuracy) {
        this.user = user;
        this.date = date;
        this.solvedQuizCount = solvedQuizCount;
        this.averageAccuracy = averageAccuracy;
    }
}