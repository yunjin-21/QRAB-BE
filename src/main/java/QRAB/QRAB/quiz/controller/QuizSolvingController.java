package QRAB.QRAB.quiz.controller;

import QRAB.QRAB.quiz.dto.QuizSolvingResponseDTO;
import QRAB.QRAB.quiz.dto.UnsolvedQuizSetResponseDTO;
import QRAB.QRAB.quiz.service.QuizService;
import QRAB.QRAB.quiz.service.QuizSolvingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping
    public ResponseEntity<Page<UnsolvedQuizSetResponseDTO>> findUnsolvedQuizSets(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<UnsolvedQuizSetResponseDTO> unsolvedQuizSets = quizService.findUnsolvedQuizSets(page);
        return ResponseEntity.ok(unsolvedQuizSets);
    }

    // 퀴즈 풀기 내 퀴즈 조회
    @GetMapping("/{quizSetId}/quizzes")
    public ResponseEntity<QuizSolvingResponseDTO> getQuizSetDetails(@PathVariable Long quizSetId) {
        QuizSolvingResponseDTO response = quizSolvingService.getQuizSetDetails(quizSetId);
        return ResponseEntity.ok(response);
    }
}
