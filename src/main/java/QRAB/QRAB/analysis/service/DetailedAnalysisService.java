package QRAB.QRAB.analysis.service;

import QRAB.QRAB.analysis.domain.CategoryAnalysis;
import QRAB.QRAB.analysis.domain.CategoryStrength;
import QRAB.QRAB.analysis.domain.DetailedAnalysis;
import QRAB.QRAB.analysis.dto.DetailedAnalysisResponseDTO;
import QRAB.QRAB.analysis.repository.CategoryAnalysisRepository;
import QRAB.QRAB.analysis.repository.CategoryStrengthRepository;
import QRAB.QRAB.analysis.repository.DetailedAnalysisRepository;
import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.category.repository.CategoryRepository;
import QRAB.QRAB.chatgpt.service.ChatgptService;
import QRAB.QRAB.quiz.domain.QuizAnswer;
import QRAB.QRAB.quiz.repository.QuizAnswerRepository;
import QRAB.QRAB.quiz.repository.QuizRepository;
import QRAB.QRAB.quiz.repository.QuizResultRepository;
import QRAB.QRAB.record.repository.RecordRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DetailedAnalysisService {
    private final ChatgptService chatgptService;
    private final CategoryAnalysisRepository categoryAnalysisRepository;
    private final DetailedAnalysisRepository detailedAnalysisRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final UserRepository userRepository;
    private final CategoryStrengthRepository categoryStrengthRepository;
    private final RecordRepository recordRepository;

    @Autowired
    public DetailedAnalysisService(ChatgptService chatgptService,
                                   CategoryAnalysisRepository categoryAnalysisRepository,
                                   DetailedAnalysisRepository detailedAnalysisRepository,
                                   QuizAnswerRepository quizAnswerRepository,
                                   UserRepository userRepository,
                                   CategoryStrengthRepository categoryStrengthRepository,
                                   RecordRepository recordRepository) {
        this.chatgptService = chatgptService;
        this.categoryAnalysisRepository = categoryAnalysisRepository;
        this.detailedAnalysisRepository = detailedAnalysisRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.userRepository = userRepository;
        this.categoryStrengthRepository = categoryStrengthRepository;
        this.recordRepository = recordRepository;
    }

    /**
     * 상세 분석 생성
     * 1. 기존 상세 분석의 isLatest를 false로 설정
     * 2. 카테고리별 강점/약점 업데이트
     * 3. 퀴즈 요약 수집
     * 4. GPT를 통한 분석 텍스트 생성
     * 5. 약점 카테고리에 대한 학습 추천 생성
     * 6. 상세 분석 저장
     * 7. 응답 DTO 생성 및 반환
     *
     * @param username 분석할 사용자의 아이디
     * @return DetailedAnalysisResponseDTO 상세 분석 결과
     */
    public DetailedAnalysisResponseDTO getDetailedAnalysis(String username) {
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. 기존 상세 분석이 있다면 isLatest를 false로 설정
        detailedAnalysisRepository.findByUserAndIsLatestTrue(user)
                .ifPresent(analysis -> {
                    analysis.setLatest(false);
                    detailedAnalysisRepository.save(analysis);
                });

        // 2. 카테고리 강약점 업데이트
        updateCategoryStrengths(user);

        // 3. 퀴즈 요약 수집
        List<String> strongCategorySummaries = getQuizSummaries(user, true);
        List<String> weakCategorySummaries = getQuizSummaries(user, false);

        // 4. GPT를 통한 분석 생성
        String userAnalysis = chatgptService.generateDetailedAnalysis(
                user.getNickname(),
                strongCategorySummaries,
                weakCategorySummaries,
                getStrongCategories(user).stream()
                        .map(this::getFinalCategoryName)
                        .collect(Collectors.toList()),
                getWeakCategories(user).stream()
                        .map(this::getFinalCategoryName)
                        .collect(Collectors.toList())
        );

        // 5. 약점 카테고리에 대한 학습 추천 생성
        List<DetailedAnalysisResponseDTO.WeakCategoryDTO> weakCategoryDTOs = generateWeakCategoryRecommendations(
                user,
                getWeakCategories(user)
        );

        // 6. 상세 분석 저장
        DetailedAnalysis detailedAnalysis = new DetailedAnalysis();
        detailedAnalysis.setUser(user);

        try {
            ObjectMapper mapper = new ObjectMapper();

            // userAnalysis를 JSON 형식으로 변환
            JsonNode userAnalysisJson = mapper.createObjectNode()
                    .put("analysis", userAnalysis);
            detailedAnalysis.setUserAnalysis(mapper.writeValueAsString(userAnalysisJson));

            // studyTips를 JSON 형식으로 변환
            Map<String, List<String>> studyTipsMap = weakCategoryDTOs.stream()
                    .collect(Collectors.toMap(
                            DetailedAnalysisResponseDTO.WeakCategoryDTO::getFinalCategoryName,
                            DetailedAnalysisResponseDTO.WeakCategoryDTO::getStudyTips
                    ));
            detailedAnalysis.setStudyTips(mapper.writeValueAsString(studyTipsMap));

            // studyReferences를 JSON 형식으로 변환
            Map<String, List<DetailedAnalysisResponseDTO.ReferenceDTO>> referencesMap = weakCategoryDTOs.stream()
                    .collect(Collectors.toMap(
                            DetailedAnalysisResponseDTO.WeakCategoryDTO::getFinalCategoryName,
                            DetailedAnalysisResponseDTO.WeakCategoryDTO::getReferences
                    ));
            detailedAnalysis.setStudyReferences(mapper.writeValueAsString(referencesMap));

            detailedAnalysis.setLatest(true);
            detailedAnalysisRepository.save(detailedAnalysis);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert to JSON", e);
        }

        // 7. 응답 DTO 생성
        return createResponseDTO(userAnalysis,
                getStrongCategories(user),
                weakCategoryDTOs);
    }

    // 약점 카테고리에 대한 학습 추천 생성
    private List<DetailedAnalysisResponseDTO.WeakCategoryDTO> generateWeakCategoryRecommendations(User user, List<Category> weakCategories) {
        return weakCategories.stream()
                .map(category -> {
                    DetailedAnalysisResponseDTO.WeakCategoryDTO dto = new DetailedAnalysisResponseDTO.WeakCategoryDTO();
                    dto.setFinalCategoryName(getFinalCategoryName(category));

                    // 학습 팁 생성
                    List<String> weakSummaries = quizAnswerRepository.findByUserAndNoteCategory(user, category)
                            .stream()
                            .filter(answer -> !answer.isCorrect())
                            .map(answer -> answer.getQuiz().getQuizSummary())
                            .collect(Collectors.toList());

                    dto.setStudyTips(chatgptService.generateStudyTips(category.getName(), weakSummaries));

                    // 추천 자료 생성
                    dto.setReferences(chatgptService.generateReferences(
                            category.getName(),
                            weakSummaries.stream()
                                    .limit(3)
                                    .collect(Collectors.joining(" "))
                    ));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 카테고리별 강점/약점 업데이트
     * 카테고리 정확도를 기준으로 상위 2개는 강점, 하위 2개는 약점으로 설정
     *
     * @param user 업데이트할 사용자
     */
    private void updateCategoryStrengths(User user) {
        // 기존 카테고리 강약점 데이터 삭제
        List<CategoryStrength> existingStrengths =
                categoryStrengthRepository.findByUser(user);
        categoryStrengthRepository.deleteAll(existingStrengths);

        // CategoryAnalysis 데이터를 기반으로 새로운 강약점 분석
        List<CategoryAnalysis> analyses =
                categoryAnalysisRepository.findByUserOrderByCategoryAccuracyDesc(user);

        // 상위 2개는 강점 카테고리로
        analyses.stream()
                .limit(2)
                .forEach(analysis -> {
                    CategoryStrength strength = new CategoryStrength();
                    strength.setUser(user);
                    strength.setCategory(analysis.getCategory());
                    strength.setStrengthType(CategoryStrength.StrengthType.STRONG);
                    categoryStrengthRepository.save(strength);
                });

        // 하위 2개는 약점 카테고리로
        analyses.stream()
                .sorted((a1, a2) ->
                        Double.compare(a1.getCategoryAccuracy(), a2.getCategoryAccuracy()))
                .limit(2)
                .forEach(analysis -> {
                    CategoryStrength strength = new CategoryStrength();
                    strength.setUser(user);
                    strength.setCategory(analysis.getCategory());
                    strength.setStrengthType(CategoryStrength.StrengthType.WEAK);
                    categoryStrengthRepository.save(strength);
                });
    }

    /**
     * 사용자의 강점 카테고리 목록 조회
     *
     * @param user 조회할 사용자
     * @return 강점 카테고리 목록
     */
    private List<Category> getStrongCategories(User user) {
        return categoryStrengthRepository
                .findByUserAndStrengthType(user, CategoryStrength.StrengthType.STRONG)
                .stream()
                .map(CategoryStrength::getCategory)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 약점 카테고리 목록 조회
     *
     * @param user 조회할 사용자
     * @return 약점 카테고리 목록
     */
    private List<Category> getWeakCategories(User user) {
        return categoryStrengthRepository
                .findByUserAndStrengthType(user, CategoryStrength.StrengthType.WEAK)
                .stream()
                .map(CategoryStrength::getCategory)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 퀴즈 요약 조회
     *
     * @param user 조회할 사용자
     * @param isStrong true면 강점 카테고리, false면 약점 카테고리의 요약을 조회
     * @return 퀴즈 요약 목록
     */
    private List<String> getQuizSummaries(User user, boolean isStrong) {
        List<Category> categories = isStrong ? getStrongCategories(user) : getWeakCategories(user);

        return categories.stream()
                .flatMap(category -> {
                    List<QuizAnswer> answers = quizAnswerRepository.findByUserAndNoteCategory(user, category);
                    if (isStrong) {
                        // 강점 카테고리면 맞은 문제만 수집
                        return answers.stream()
                                .filter(QuizAnswer::isCorrect)
                                .map(answer -> answer.getQuiz().getQuizSummary());
                    } else {
                        // 약점 카테고리면 틀린 문제만 수집
                        return answers.stream()
                                .filter(answer -> !answer.isCorrect())
                                .map(answer -> answer.getQuiz().getQuizSummary());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 카테고리의 최상위 카테고리명 반환
     *
     * @param category 대상 카테고리
     * @return 부모 카테고리가 있으면 부모 카테고리명, 없으면 현재 카테고리명
     */
    private String getFinalCategoryName(Category category) {
        return category.getParentCategory() != null ?
                category.getParentCategory().getName() :
                category.getName();
    }

    // 상세 분석 응답 DTO 생성
    private DetailedAnalysisResponseDTO createResponseDTO(
            String userAnalysis,
            List<Category> strongCategories,
            List<DetailedAnalysisResponseDTO.WeakCategoryDTO> weakCategoryDTOs) {
        DetailedAnalysisResponseDTO responseDTO = new DetailedAnalysisResponseDTO();
        responseDTO.setUserAnalysis(userAnalysis);

        DetailedAnalysisResponseDTO.StrongCategoriesDTO strongCategoriesDTO =
                new DetailedAnalysisResponseDTO.StrongCategoriesDTO();
        strongCategoriesDTO.setFinalCategoryName(
                strongCategories.stream()
                        .map(this::getFinalCategoryName)
                        .collect(Collectors.toList())
        );
        responseDTO.setStrongCategories(strongCategoriesDTO);
        responseDTO.setWeakCategories(weakCategoryDTOs);

        return responseDTO;
    }

    /**
    *상세 분석 조회
     */
    public DetailedAnalysisResponseDTO getLatestDetailedAnalysis(String username) {
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DetailedAnalysis latestAnalysis = detailedAnalysisRepository.findByUserAndIsLatestTrue(user)
                .orElseThrow(() -> new RuntimeException("No analysis found"));

        try {
            ObjectMapper mapper = new ObjectMapper();

            // userAnalysis JSON 파싱
            JsonNode userAnalysisNode = mapper.readTree(latestAnalysis.getUserAnalysis());
            String userAnalysis = userAnalysisNode.get("analysis").asText();

            // studyTips JSON 파싱
            Map<String, List<String>> studyTipsMap = mapper.readValue(
                    latestAnalysis.getStudyTips(),
                    new TypeReference<Map<String, List<String>>>() {}
            );

            // studyReferences JSON 파싱
            Map<String, List<DetailedAnalysisResponseDTO.ReferenceDTO>> referencesMap = mapper.readValue(
                    latestAnalysis.getStudyReferences(),
                    new TypeReference<Map<String, List<DetailedAnalysisResponseDTO.ReferenceDTO>>>() {}
            );

            // WeakCategoryDTO 리스트 생성
            List<DetailedAnalysisResponseDTO.WeakCategoryDTO> weakCategoryDTOs = getWeakCategories(user).stream()
                    .map(category -> {
                        String categoryName = getFinalCategoryName(category);
                        DetailedAnalysisResponseDTO.WeakCategoryDTO dto = new DetailedAnalysisResponseDTO.WeakCategoryDTO();
                        dto.setFinalCategoryName(categoryName);
                        dto.setStudyTips(studyTipsMap.get(categoryName));
                        dto.setReferences(referencesMap.get(categoryName));
                        return dto;
                    })
                    .collect(Collectors.toList());

            return createResponseDTO(
                    userAnalysis,
                    getStrongCategories(user),
                    weakCategoryDTOs
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON data", e);
        }
    }

    /**
     * 3개월 이상 된 상세 분석 기록을 삭제
     * 매일 자정에 실행되며, 당일 로그인한 사용자의 기록만 처리
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpOldRecords() {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 당일 로그인한 사용자들의 Record 조회
        List<QRAB.QRAB.record.domain.Record> todayRecords = recordRepository.findByLoginDate(today);

        // Record에서 User 목록 추출 (중복 제거)
        List<User> todayActiveUsers = todayRecords.stream()
                .map(QRAB.QRAB.record.domain.Record::getUser)
                .distinct()
                .collect(Collectors.toList());

        // 3개월 이전 날짜 계산
        LocalDateTime threshold = LocalDateTime.now().minusMonths(3);

        // 오늘 로그인한 사용자들의 3개월 이상된 분석 기록만 삭제
        todayActiveUsers.forEach(user -> {
            List<DetailedAnalysis> oldRecords = detailedAnalysisRepository
                    .findByUserAndCreatedAtBeforeAndIsLatestFalse(user, threshold);
            detailedAnalysisRepository.deleteAll(oldRecords);
        });
    }
}