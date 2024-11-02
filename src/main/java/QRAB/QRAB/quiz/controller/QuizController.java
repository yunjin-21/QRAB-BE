package QRAB.QRAB.quiz.controller;

import QRAB.QRAB.note.dto.QuizLabNoteResponseDTO;
import QRAB.QRAB.note.service.NoteService;
import QRAB.QRAB.quiz.dto.QuizResultDTO;
import QRAB.QRAB.quiz.dto.QuizSetDTO;
import QRAB.QRAB.quiz.dto.QuizGenerationRequestDTO;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/quiz-lab")
public class QuizController {
    private final QuizService quizService;
    private final NoteService noteService;

    @Autowired
    public QuizController(QuizService quizService, NoteService noteService){
        this.quizService = quizService;
        this.noteService = noteService;
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

    // 저장된 노트 리스트 조회 엔드포인트 (퀴즈 연구소 화면용)
    @GetMapping("/notes")
    public ResponseEntity<List<QuizLabNoteResponseDTO>> getStoredNotesForQuizLab(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        // 인증된 사용자의 정보를 SecurityContextHolder를 통해 가져오는 부분을 유지
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 노트 목록 조회
        List<QuizLabNoteResponseDTO> storedNotes = noteService.getStoredNotesForQuizLab(username, page);
        return ResponseEntity.ok(storedNotes);
    }

    // status가 solved인 퀴즈 세트 조회 엔드포인트 (퀴즈 저장소 화면용)
    @GetMapping("/quizzes")
    public ResponseEntity<Page<QuizResultDTO>> getSolvedQuizSets(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<QuizResultDTO> solvedQuizSets = quizService.getSolvedQuizSets(page);
        return ResponseEntity.ok(solvedQuizSets);
    }


}
