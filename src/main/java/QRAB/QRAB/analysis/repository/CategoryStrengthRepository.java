package QRAB.QRAB.analysis.repository;

import QRAB.QRAB.analysis.domain.CategoryStrength;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryStrengthRepository extends JpaRepository<CategoryStrength, Long> {
    List<CategoryStrength> findByUserAndStrengthType(User user, CategoryStrength.StrengthType strengthType);
    List<CategoryStrength> findByUser(User user);
}
