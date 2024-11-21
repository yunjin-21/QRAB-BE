package QRAB.QRAB.analysis.domain;

import QRAB.QRAB.BaseTimeEntity;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.category.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "detailed_analysis")
public class DetailedAnalysis extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailedAnalysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "JSON")
    private String userAnalysis;

    @Column(columnDefinition = "TEXT")
    private String studyTips;

    @Column(name = "study_references", columnDefinition = "JSON")
    private String studyReferences;

    private boolean isLatest = true;
}