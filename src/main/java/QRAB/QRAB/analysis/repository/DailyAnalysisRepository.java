package QRAB.QRAB.analysis.repository;

import QRAB.QRAB.analysis.domain.DailyAnalysis;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyAnalysisRepository extends JpaRepository<DailyAnalysis, Long> {
    Optional<DailyAnalysis> findByUserAndDate(User user, LocalDate date);

    @Query("SELECT da FROM DailyAnalysis da WHERE da.user = :user AND FUNCTION('DATE_FORMAT', da.date, '%Y-%m') = :month")
    List<DailyAnalysis> findByUserAndMonth(@Param("user") User user, @Param("month") String month);

    @Query("SELECT SUM(da.solvedQuizCount) FROM DailyAnalysis da WHERE da.user = :user AND da.date BETWEEN :startDate AND :endDate")
    int getTotalSolvedQuizCountBetweenDates(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT AVG(da.averageAccuracy) FROM DailyAnalysis da WHERE da.user = :user AND da.date BETWEEN :startDate AND :endDate")
    float getAverageAccuracyBetweenDates(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}