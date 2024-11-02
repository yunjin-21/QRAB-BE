package QRAB.QRAB.quiz.controller;

import QRAB.QRAB.quiz.dto.UnsolvedQuizSetResponseDTO;
import QRAB.QRAB.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz-solving")
public class QuizSolvingController {

    private final QuizService quizService;

    @Autowired
    public QuizSolvingController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<Page<UnsolvedQuizSetResponseDTO>> findUnsolvedQuizSets(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<UnsolvedQuizSetResponseDTO> unsolvedQuizSets = quizService.findUnsolvedQuizSets(page);
        return ResponseEntity.ok(unsolvedQuizSets);
    }
}
