package QRAB.QRAB.analysis.repository;

import QRAB.QRAB.analysis.domain.DetailedAnalysis;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetailedAnalysisRepository extends JpaRepository<DetailedAnalysis, Long> {
    Optional<DetailedAnalysis> findByUserAndIsLatestTrue(User user);
    List<DetailedAnalysis> findByUserAndCreatedAtBeforeAndIsLatestFalse(User user, LocalDateTime date);
}