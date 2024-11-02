package QRAB.QRAB.quiz.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizSolvingService {

    private final QuizRepository quizRepository;
    private final QuizSetRepository quizSetRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    @Autowired
    public QuizSolvingService(QuizRepository quizRepository, QuizSetRepository quizSetRepository,
                              QuizResultRepository quizResultRepository, QuizAnswerRepository quizAnswerRepository) {
        this.quizRepository = quizRepository;
        this.quizSetRepository = quizSetRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizAnswerRepository = quizAnswerRepository;
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

        int correctCount = 0;
        List<QuizGradingResponseDTO.QuizResultDetailDTO> quizResults = new ArrayList<>();

        // 점수 계산에 필요한 데이터를 먼저 수집
        for (QuizGradingRequestDTO.AnswerDTO answer : request.getAnswers()) {
            Quiz quiz = quizRepository.findById(answer.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            // 정답 여부 확인
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

        // 총 질문 수와 점수 계산
        int totalQuestions = request.getAnswers().size();
        int score = (int) ((double) correctCount / totalQuestions * 100);

        // QuizResult 엔티티 저장
        QuizResult quizResult = new QuizResult();
        quizResult.setQuizSet(quizSet);
        quizResult.setUser(quizSet.getUser());
        quizResult.setScore(score);
        quizResult.setCorrectCount(correctCount);
        quizResult.setTotalQuestions(totalQuestions);
        quizResult.setTakenAt(LocalDateTime.now());
        quizResultRepository.save(quizResult);

        // 각 QuizAnswer에 quizResult 설정 후 저장
        for (QuizGradingRequestDTO.AnswerDTO answer : request.getAnswers()) {
            Quiz quiz = quizRepository.findById(answer.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));
            boolean isCorrect = (quiz.getCorrectAnswer() == answer.getSelectedAnswer());

            QuizAnswer quizAnswer = new QuizAnswer();
            quizAnswer.setQuizResult(quizResult); // QuizResult 설정
            quizAnswer.setQuiz(quiz);
            quizAnswer.setSelectedAnswer(answer.getSelectedAnswer());
            quizAnswer.setIsCorrect(isCorrect);
            quizAnswerRepository.save(quizAnswer);
        }

        // 응답 DTO 준비
        QuizGradingResponseDTO response = new QuizGradingResponseDTO();
        response.setNoteTitle(quizSet.getNote().getTitle());
        response.setScore(score);
        response.setCorrectCount(correctCount);
        response.setTotalQuestions(totalQuestions);
        response.setTakenAt(LocalDateTime.now());
        response.setQuizzes(quizResults);

        return response;
    }

}
