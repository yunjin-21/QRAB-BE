package QRAB.QRAB.analysis.service;

import QRAB.QRAB.analysis.domain.DailyAnalysis;
import QRAB.QRAB.analysis.repository.DailyAnalysisRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.util.SecurityUtil;
import QRAB.QRAB.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

        dailyAnalysis.setSolvedQuizCount(dailyAnalysis.getSolvedQuizCount() + solvedQuizCount);

        float totalAccuracy = (dailyAnalysis.getAverageAccuracy() * dailyAnalysis.getSolvedQuizCount())
                + (accuracy * solvedQuizCount);
        dailyAnalysis.setAverageAccuracy(totalAccuracy / (dailyAnalysis.getSolvedQuizCount() + solvedQuizCount));

        dailyAnalysisRepository.save(dailyAnalysis);
    }
}