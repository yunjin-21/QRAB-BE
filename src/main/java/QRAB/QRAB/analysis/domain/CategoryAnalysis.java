package QRAB.QRAB.analysis.domain;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "category_analysis")
@Data
public class CategoryAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryAnalysisId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 7)
    private String month; // YYYY-MM 형식

    @Column(nullable = false)
    private int solvedQuizCount;

    @Column(nullable = false)
    private float categoryAccuracy;
}

