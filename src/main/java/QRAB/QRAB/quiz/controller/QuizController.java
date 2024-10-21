package QRAB.QRAB.quiz.controller;

import QRAB.QRAB.quiz.dto.QuizSetDTO;
import QRAB.QRAB.quiz.dto.QuizGenerationRequestDTO;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/quiz-lab")
public class QuizController {
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService){
        this.quizService = quizService;
    }

    // 퀴즈 세트 생성 엔드포인트
    @PostMapping("/generate")
    public ResponseEntity<QuizSetDTO> generateQuizSet(@RequestBody QuizGenerationRequestDTO requestDTO){
        QuizSetDTO quizSetDTO = quizService.createQuizSet(requestDTO);
        return ResponseEntity.ok(quizSetDTO);
    }

    // 특정 퀴즈 세트의 퀴즈 조회 엔드포인트
    @GetMapping("/{quizSetId}/quizzes")
    public ResponseEntity<List<Quiz>> getQuizzesByQuizSetId(@PathVariable Long quizSetId){
        List<Quiz> quizzes = quizService.getQuizzesByQuizSetId(quizSetId);
        return ResponseEntity.ok(quizzes);
    }

}
