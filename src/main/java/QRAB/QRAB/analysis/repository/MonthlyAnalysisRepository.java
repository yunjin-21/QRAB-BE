package QRAB.QRAB.analysis.repository;

import QRAB.QRAB.analysis.domain.MonthlyAnalysis;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyAnalysisRepository extends JpaRepository<MonthlyAnalysis, Long> {

    Optional<MonthlyAnalysis> findByUserAndMonth(User user, String month);

    List<MonthlyAnalysis> findAllByUser(User user);//특정 유저의 모든 월별 데이터 조회
}


