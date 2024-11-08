package QRAB.QRAB.quiz.controller;

import QRAB.QRAB.quiz.dto.*;
import QRAB.QRAB.quiz.service.QuizService;
import QRAB.QRAB.quiz.service.QuizSolvingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/quiz-solving")
public class QuizSolvingController {

    private final QuizService quizService;
    private final QuizSolvingService quizSolvingService;

    @Autowired
    public QuizSolvingController(QuizService quizService, QuizSolvingService quizSolvingService) {
        this.quizService = quizService;
        this.quizSolvingService = quizSolvingService;
    }

    // 퀴즈 풀기 전체 퀴즈세트 조회
    @GetMapping("/all/quizzes")
    public ResponseEntity<Page<UnsolvedQuizSetResponseDTO>> findUnsolvedQuizSets(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<UnsolvedQuizSetResponseDTO> unsolvedQuizSets = quizService.findUnsolvedQuizSets(page);
        return ResponseEntity.ok(unsolvedQuizSets);
    }

    // 특정 노트 unsolved 퀴즈 세트 조회
    @GetMapping("/{noteId}/unsolved")
    public ResponseEntity<Map<String, Object>> getUnsolvedQuizSets(
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "0") int page
    ) {
        Map<String, Object> response = quizService.findUnsolvedQuizSetsByNoteId(noteId, page);
        return ResponseEntity.ok(response);
    }


    // 퀴즈 풀기 내 퀴즈 조회
    @GetMapping("/{quizSetId}/quizzes")
    public ResponseEntity<QuizSolvingResponseDTO> getQuizSetDetails(@PathVariable Long quizSetId) {
        QuizSolvingResponseDTO response = quizSolvingService.getQuizSetDetails(quizSetId);
        return ResponseEntity.ok(response);
    }

    // 퀴즈 채점
    @PostMapping("/{quizSetId}/quizzes/grade")
    public ResponseEntity<QuizGradingResponseDTO> gradeQuizAnswers(
            @PathVariable ("quizSetId")Long quizSetId,
            @RequestBody QuizGradingRequestDTO request) {
        QuizGradingResponseDTO result = quizSolvingService.evaluateQuizSet(quizSetId, request);
        return ResponseEntity.ok(result);
    }
}
