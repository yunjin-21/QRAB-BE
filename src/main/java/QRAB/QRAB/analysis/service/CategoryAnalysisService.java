package QRAB.QRAB.analysis.service;

import QRAB.QRAB.analysis.domain.CategoryAnalysis;
import QRAB.QRAB.analysis.repository.CategoryAnalysisRepository;
import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.user.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

