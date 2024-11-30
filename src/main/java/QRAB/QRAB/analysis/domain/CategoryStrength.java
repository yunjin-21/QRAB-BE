package QRAB.QRAB.analysis.domain;

import QRAB.QRAB.BaseTimeEntity;
import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "category_strength")
public class CategoryStrength extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "strength_type")
    private StrengthType strengthType;

    public enum StrengthType {
        STRONG, WEAK
    }
}