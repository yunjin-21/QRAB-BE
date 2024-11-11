package QRAB.QRAB.analysis.repository;

import QRAB.QRAB.analysis.domain.CategoryAnalysis;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryAnalysisRepository extends JpaRepository<CategoryAnalysis, Long> {

    @Query("SELECT ca FROM CategoryAnalysis ca WHERE ca.user = :user AND ca.month = :month")
    List<CategoryAnalysis> findByUserAndMonth(@Param("user") User user, @Param("month") String month);

    @Query("SELECT ca FROM CategoryAnalysis ca WHERE ca.user = :user AND ca.category.id = :categoryId AND ca.month = :month")
    Optional<CategoryAnalysis> findByUserAndCategoryAndMonth(@Param("user") User user, @Param("categoryId") Long categoryId, @Param("month") String month);

}
