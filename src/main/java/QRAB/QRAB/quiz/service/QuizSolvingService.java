package QRAB.QRAB.quiz.service;

import QRAB.QRAB.analysis.service.CategoryAnalysisService;
import QRAB.QRAB.analysis.service.DailyAnalysisService;
import QRAB.QRAB.analysis.service.MonthlyAnalysisService;
import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizAnswer;
import QRAB.QRAB.quiz.domain.QuizResult;
import QRAB.QRAB.quiz.domain.QuizSet;
import QRAB.QRAB.quiz.dto.QuizGradingRequestDTO;
import QRAB.QRAB.quiz.dto.QuizGradingResponseDTO;
import QRAB.QRAB.quiz.dto.QuizSolvingResponseDTO;
import QRAB.QRAB.quiz.repository.QuizRepository;
import QRAB.QRAB.quiz.repository.QuizSetRepository;
import QRAB.QRAB.quiz.repository.QuizAnswerRepository;
import QRAB.QRAB.quiz.repository.QuizResultRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizSolvingService {

    private final QuizRepository quizRepository;
    private final QuizSetRepository quizSetRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final DailyAnalysisService dailyAnalysisService;
    private final MonthlyAnalysisService monthlyAnalysisService; // 추가
    private final CategoryAnalysisService categoryAnalysisService;

    @Autowired
    public QuizSolvingService(QuizRepository quizRepository, QuizSetRepository quizSetRepository,
                              QuizResultRepository quizResultRepository, QuizAnswerRepository quizAnswerRepository,
                              DailyAnalysisService dailyAnalysisService, MonthlyAnalysisService monthlyAnalysisService,
                              CategoryAnalysisService categoryAnalysisService) {
        this.quizRepository = quizRepository;
        this.quizSetRepository = quizSetRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.dailyAnalysisService = dailyAnalysisService;
        this.monthlyAnalysisService = monthlyAnalysisService;
        this.categoryAnalysisService = categoryAnalysisService;
    }

    public QuizSolvingResponseDTO getQuizSetDetails(Long quizSetId) {
        QuizSet quizSet = quizSetRepository.findById(quizSetId)
                .orElseThrow(() -> new RuntimeException("Quiz set not found"));
        Note note = quizSet.getNote();

        List<QuizSolvingResponseDTO.QuizDTO> quizzes = quizSet.getQuizzes().stream()
                .map(quiz -> new QuizSolvingResponseDTO.QuizDTO(
                        quiz.getQuizId(),
                        quizSet.getQuizSetId(),
                        quiz.getQuestion(),
                        quiz.getChoicesAsList(),
                        quiz.getDifficulty()
                ))
                .collect(Collectors.toList());

        return new QuizSolvingResponseDTO(note.getTitle(), quizzes);
    }

    public QuizGradingResponseDTO evaluateQuizSet(Long quizSetId, QuizGradingRequestDTO request) {
        QuizSet quizSet = quizSetRepository.findById(quizSetId)
                .orElseThrow(() -> new RuntimeException("Quiz Set not found"));

        // 전체 문제 수와 맞은 문제 수 계산
        int totalQuizCount = request.getAnswers().size();
        int correctCount = 0;
        List<QuizGradingResponseDTO.QuizResultDetailDTO> quizResults = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        String month = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 퀴즈 채점 및 상세 결과 생성
        for (QuizGradingRequestDTO.AnswerDTO answer : request.getAnswers()) {
            Quiz quiz = quizRepository.findById(answer.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            boolean isCorrect = (quiz.getCorrectAnswer() == answer.getSelectedAnswer());
            if (isCorrect) {
                correctCount++;
            }

            // 응답 DTO에 상세 정보 추가
            QuizGradingResponseDTO.QuizResultDetailDTO detail = new QuizGradingResponseDTO.QuizResultDetailDTO();
            detail.setQuizId(quiz.getQuizId());
            detail.setDifficulty(quiz.getDifficulty());
            detail.setQuestion(quiz.getQuestion());
            detail.setChoices(quiz.getChoicesAsList());
            detail.setSelectedAnswer(answer.getSelectedAnswer());
            detail.setCorrectAnswer(quiz.getCorrectAnswer());
            detail.setIsCorrect(isCorrect);
            detail.setExplanation(quiz.getExplanation());
            quizResults.add(detail);
        }

        // 점수와 정답률 계산
        float accuracyRate = (float) correctCount / totalQuizCount;
        int score = (int) (accuracyRate * 100);

        // 분석 데이터 업데이트
        dailyAnalysisService.updateDailyAnalysis(
                currentDate,
                totalQuizCount,
                accuracyRate
        );

        monthlyAnalysisService.updateMonthlyAnalysis(currentDate);

        Category category = quizSet.getNote().getCategory();
        categoryAnalysisService.updateCategoryAnalysis(
                category.getId(),
                month,
                totalQuizCount,
                accuracyRate
        );

        // QuizResult 엔티티 저장
        QuizResult quizResult = new QuizResult();
        quizResult.setQuizSet(quizSet);
        quizResult.setUser(quizSet.getUser());
        quizResult.setScore(score);
        quizResult.setCorrectCount(correctCount);
        quizResult.setTotalQuestions(totalQuizCount);
        quizResult.setTakenAt(LocalDateTime.now());
        quizResultRepository.save(quizResult);

        // QuizSet 업데이트
        quizSet.setStatus("solved");
        quizSet.setAccuracyRate(accuracyRate);
        quizSetRepository.save(quizSet);

        // QuizAnswer 저장
        for (QuizGradingRequestDTO.AnswerDTO answer : request.getAnswers()) {
            Quiz quiz = quizRepository.findById(answer.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));
            boolean isCorrect = (quiz.getCorrectAnswer() == answer.getSelectedAnswer());

            QuizAnswer quizAnswer = new QuizAnswer();
            quizAnswer.setQuizResult(quizResult);
            quizAnswer.setQuiz(quiz);
            quizAnswer.setQuizSet(quiz.getQuizSet());
            quizAnswer.setSelectedAnswer(answer.getSelectedAnswer());
            quizAnswer.setIsCorrect(isCorrect);
            quizAnswerRepository.save(quizAnswer);
        }

        // 응답 DTO 생성
        QuizGradingResponseDTO response = new QuizGradingResponseDTO();
        response.setQuizSetId(quizSetId);
        response.setNoteTitle(quizSet.getNote().getTitle());
        response.setScore(score);
        response.setCorrectCount(correctCount);
        response.setTotalQuestions(totalQuizCount);
        response.setTakenAt(LocalDateTime.now());
        response.setQuizzes(quizResults);

        return response;
    }
}
