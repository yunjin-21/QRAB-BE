package QRAB.QRAB.analysis.service;

import QRAB.QRAB.analysis.domain.MonthlyAnalysis;
import QRAB.QRAB.analysis.dto.MonthlyAnalysisResponseDTO;
import QRAB.QRAB.analysis.dto.CategoryAnalysisResponseDTO;
import QRAB.QRAB.analysis.repository.CategoryAnalysisRepository;
import QRAB.QRAB.analysis.repository.MonthlyAnalysisRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.user.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final MonthlyAnalysisRepository monthlyAnalysisRepository;
    private final CategoryAnalysisRepository categoryAnalysisRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnalysisService(MonthlyAnalysisRepository monthlyAnalysisRepository,
                           CategoryAnalysisRepository categoryAnalysisRepository,
                           UserRepository userRepository) {
        this.monthlyAnalysisRepository = monthlyAnalysisRepository;
        this.categoryAnalysisRepository = categoryAnalysisRepository;
        this.userRepository = userRepository;
    }

    public MonthlyAnalysisResponseDTO getMonthlyAnalysis(int year, int month) {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        String formattedMonth = String.format("%d-%02d", year, month);

        MonthlyAnalysis monthlyAnalysis = monthlyAnalysisRepository
                .findByUserAndMonth(user, formattedMonth)
                .orElseThrow(() -> new RuntimeException("월별 분석 데이터를 찾을 수 없습니다."));

        List<CategoryAnalysisResponseDTO> categories = categoryAnalysisRepository.findByUserAndMonth(user, formattedMonth).stream()
                .map(categoryAnalysis -> new CategoryAnalysisResponseDTO(
                        categoryAnalysis.getCategory().getParentCategory() != null
                                ? categoryAnalysis.getCategory().getParentCategory().getName()
                                : null,
                        categoryAnalysis.getCategory().getName(),
                        categoryAnalysis.getSolvedQuizCount(),
                        categoryAnalysis.getCategoryAccuracy()
                ))
                .collect(Collectors.toList());

        return new MonthlyAnalysisResponseDTO(
                monthlyAnalysis.getLearningDays(),
                monthlyAnalysis.getSolvedQuizCount(),
                monthlyAnalysis.getAverageAccuracy(),
                categories
        );
    }
}

