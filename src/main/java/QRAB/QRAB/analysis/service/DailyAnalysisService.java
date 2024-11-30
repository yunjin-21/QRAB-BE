package QRAB.QRAB.analysis.service;

import QRAB.QRAB.analysis.domain.DailyAnalysis;
import QRAB.QRAB.analysis.dto.MonthlySummaryResponseDTO;
import QRAB.QRAB.analysis.repository.DailyAnalysisRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.util.SecurityUtil;
import QRAB.QRAB.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyAnalysisService {

    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final UserRepository userRepository;

    public DailyAnalysisService(DailyAnalysisRepository dailyAnalysisRepository, UserRepository userRepository) {
        this.dailyAnalysisRepository = dailyAnalysisRepository;
        this.userRepository = userRepository;
    }

    public void updateDailyAnalysis(LocalDate date, int solvedQuizCount, float accuracy) {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        DailyAnalysis dailyAnalysis = dailyAnalysisRepository.findByUserAndDate(user, date)
                .orElseGet(() -> new DailyAnalysis(user, date, 0, 0.0f));

        // 기존 데이터와 새로운 데이터를 합산해 평균 계산
        int newTotalCount = dailyAnalysis.getSolvedQuizCount() + solvedQuizCount;
        float weightedAccuracy = (dailyAnalysis.getAverageAccuracy() * dailyAnalysis.getSolvedQuizCount()
                + accuracy * solvedQuizCount) / newTotalCount;

        dailyAnalysis.setSolvedQuizCount(newTotalCount);
        dailyAnalysis.setAverageAccuracy(weightedAccuracy);

        dailyAnalysisRepository.save(dailyAnalysis);
    }

    public MonthlySummaryResponseDTO getMonthlySummary(int year, int month) {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        // 연월 조회 (yyyy-mm 형식)
        String monthFormatted = String.format("%04d-%02d", year, month);
        List<DailyAnalysis> dailyAnalyses = dailyAnalysisRepository.findByUserAndMonth(user, monthFormatted);

        // DailyAnalysis -> DailyRecord 변환
        List<MonthlySummaryResponseDTO.DailyRecord> dailyRecords = dailyAnalyses.stream()
                .map(daily -> new MonthlySummaryResponseDTO.DailyRecord(
                        daily.getDate().toString(),
                        daily.getSolvedQuizCount(),
                        daily.getAverageAccuracy()
                ))
                .toList();

        return new MonthlySummaryResponseDTO(year, month, dailyRecords);
    }
}