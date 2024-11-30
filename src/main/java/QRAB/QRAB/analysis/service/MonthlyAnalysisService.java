package QRAB.QRAB.analysis.service;

import QRAB.QRAB.analysis.domain.DailyAnalysis;
import QRAB.QRAB.analysis.domain.MonthlyAnalysis;
import QRAB.QRAB.analysis.repository.DailyAnalysisRepository;
import QRAB.QRAB.analysis.repository.MonthlyAnalysisRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.user.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MonthlyAnalysisService {

    private final MonthlyAnalysisRepository monthlyAnalysisRepository;
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final UserRepository userRepository;

    @Autowired
    public MonthlyAnalysisService(MonthlyAnalysisRepository monthlyAnalysisRepository,
                                  DailyAnalysisRepository dailyAnalysisRepository,
                                  UserRepository userRepository) {
        this.monthlyAnalysisRepository = monthlyAnalysisRepository;
        this.dailyAnalysisRepository = dailyAnalysisRepository;
        this.userRepository = userRepository;
    }

    public void updateMonthlyAnalysis(LocalDate date) {
        // 인증된 사용자 가져오기
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        String currentMonth = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // DailyAnalysis 데이터를 기반으로 월별 통계 계산
        List<DailyAnalysis> dailyAnalyses = dailyAnalysisRepository.findByUserAndMonth(user, currentMonth);

        int totalSolvedQuizCount = dailyAnalyses.stream()
                .mapToInt(DailyAnalysis::getSolvedQuizCount)
                .sum();

        // totalSolvedQuizCount가 0일 경우 예외 처리 (0으로 나누는 오류 방지)
        if (totalSolvedQuizCount == 0) {
            throw new RuntimeException("No quiz data found for the current month");
        }

        float totalAccuracy = (float) dailyAnalyses.stream()
                .mapToDouble(daily -> daily.getAverageAccuracy() * daily.getSolvedQuizCount())
                .sum() / totalSolvedQuizCount; // 정확도 평균 계산

        int learningDays = dailyAnalyses.size(); // 학습 일수는 DailyAnalysis 개수로 계산

        // 기존 MonthlyAnalysis 가져오거나 생성
        MonthlyAnalysis monthlyAnalysis = monthlyAnalysisRepository.findByUserAndMonth(user, currentMonth)
                .orElse(new MonthlyAnalysis(user, currentMonth, 0, 0, 0.0f));

        // 통계 업데이트
        monthlyAnalysis.setSolvedQuizCount(totalSolvedQuizCount);
        monthlyAnalysis.setLearningDays(learningDays);
        monthlyAnalysis.setAverageAccuracy(totalAccuracy);

        // 저장
        monthlyAnalysisRepository.save(monthlyAnalysis);
    }
}
