package QRAB.QRAB.analysis.service;

import QRAB.QRAB.analysis.domain.CategoryAnalysis;
import QRAB.QRAB.analysis.dto.CategoryAnalysisResponseDTO;
import QRAB.QRAB.analysis.repository.CategoryAnalysisRepository;
import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.user.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryAnalysisService {

    private final CategoryAnalysisRepository categoryAnalysisRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryAnalysisService(CategoryAnalysisRepository categoryAnalysisRepository,
                                   UserRepository userRepository) {
        this.categoryAnalysisRepository = categoryAnalysisRepository;
        this.userRepository = userRepository;
    }

    public void updateCategoryAnalysis(Long categoryId, String month, int solvedQuizCount, float accuracy) {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        CategoryAnalysis categoryAnalysis = categoryAnalysisRepository
                .findByUserAndCategoryAndMonth(user, categoryId, month)
                .orElseGet(() -> {
                    CategoryAnalysis newAnalysis = new CategoryAnalysis();
                    newAnalysis.setUser(user);
                    newAnalysis.setCategory(new Category(categoryId)); // Category 엔티티 참조
                    newAnalysis.setMonth(month);
                    newAnalysis.setSolvedQuizCount(0);
                    newAnalysis.setCategoryAccuracy(0.0f);
                    return newAnalysis;
                });

        categoryAnalysis.setSolvedQuizCount(categoryAnalysis.getSolvedQuizCount() + solvedQuizCount);
        float totalAccuracy = (categoryAnalysis.getCategoryAccuracy() * categoryAnalysis.getSolvedQuizCount())
                + (accuracy * solvedQuizCount);
        categoryAnalysis.setCategoryAccuracy(totalAccuracy / (categoryAnalysis.getSolvedQuizCount() + solvedQuizCount));

        categoryAnalysisRepository.save(categoryAnalysis);
    }

    // 카테고리별 학습 분석 조회
    public List<CategoryAnalysisResponseDTO> getCategoryAnalysis(String period) {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        LocalDate now = LocalDate.now();
        String startMonth;
        switch (period) {
            case "1_month":
                startMonth = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
                break;
            case "3_months":
                startMonth = now.minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM"));
                break;
            case "6_months":
                startMonth = now.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM"));
                break;
            case "overall":
                startMonth = "0000-00"; // 전체 기간 조회
                break;
            default:
                throw new IllegalArgumentException("Invalid period specified: " + period);
        }

        // 기간에 해당하는 데이터 조회
        List<CategoryAnalysis> analyses = categoryAnalysisRepository.findByUserAndMonthGreaterThanEqual(user, startMonth);

        // 카테고리 별로 그룹화해서 푼 퀴즈 수 합과 정답률 평균 계산
        Map<Long, CategoryAnalysisResponseDTO> groupedResults = new HashMap<>();
        for (CategoryAnalysis analysis : analyses) {
            Long categoryId = analysis.getCategory().getId();

            // 기존 카테고리가 있다면 합산
            if (groupedResults.containsKey(categoryId)) {
                CategoryAnalysisResponseDTO existing = groupedResults.get(categoryId);
                int totalSolvedCount = existing.getSolvedQuizCount() + analysis.getSolvedQuizCount();
                float totalAccuracy = (existing.getCategoryAccuracy() * existing.getSolvedQuizCount())
                        + (analysis.getCategoryAccuracy() * analysis.getSolvedQuizCount());
                float avgAccuracy = totalAccuracy / totalSolvedCount;

                existing.setSolvedQuizCount(totalSolvedCount);
                existing.setCategoryAccuracy(avgAccuracy);
            } else {
                // 새로운 카테고리인 경우 추가
                groupedResults.put(categoryId, new CategoryAnalysisResponseDTO(
                        analysis.getCategory().getParentCategory() != null
                                ? analysis.getCategory().getParentCategory().getName() : null,
                        analysis.getCategory().getName(),
                        analysis.getSolvedQuizCount(),
                        analysis.getCategoryAccuracy()
                ));
            }
        }

        return new ArrayList<>(groupedResults.values());
    }
}

