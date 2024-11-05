package QRAB.QRAB.quiz.controller;

import QRAB.QRAB.note.controller.NoteController;
import QRAB.QRAB.note.dto.QuizLabNoteResponseDTO;
import QRAB.QRAB.note.service.NoteService;
import QRAB.QRAB.quiz.dto.QuizGradingResponseDTO;
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
    private final NoteController noteController;

    @Autowired
    public QuizController(QuizService quizService, NoteService noteService, NoteController noteController){
        this.quizService = quizService;
        this.noteService = noteService;
        this.noteController = noteController;
    }

    // 퀴즈 세트 생성 엔드포인트
    @PostMapping("/generate")
    public ResponseEntity<QuizSetDTO> generateQuizSet(@RequestBody QuizGenerationRequestDTO requestDTO){
        QuizSetDTO quizSetDTO = quizService.createQuizSet(requestDTO);
        return ResponseEntity.ok(quizSetDTO);
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

    @GetMapping("/quizzes/{categoryId}")
    public ResponseEntity<?> getNotePageByCategoryInQuizLab(@PathVariable("categoryId") Long categoryId,
                                                            @RequestParam(name = "page", defaultValue = "0") int page) {
        return noteController.getNotePageByCategory(categoryId, page);
    }

    @GetMapping("/quizzes/{noteId}/solved")
    public ResponseEntity<Page<QuizResultDTO>> getSolvedQuizSetsByNoteId(
            @PathVariable("noteId") Long noteId,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<QuizResultDTO> solvedQuizSets = quizService.getSolvedQuizSetsByNoteId(noteId, page);
        return ResponseEntity.ok(solvedQuizSets);
    }

    // status가 solved인 퀴즈 세트 조회 엔드포인트 (퀴즈 저장소 화면용)
    @GetMapping("/quizzes/solved")
    public ResponseEntity<Page<QuizResultDTO>> getSolvedQuizSets(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<QuizResultDTO> solvedQuizSets = quizService.getSolvedQuizSets(page);
        return ResponseEntity.ok(solvedQuizSets);
    }

    // 특정 퀴즈 세트의 채점 결과 조회 엔드포인트
    @GetMapping("/quizzes/solved/{quizSetId}/result")
    public ResponseEntity<QuizGradingResponseDTO> getQuizSetResult(@PathVariable Long quizSetId) {
        QuizGradingResponseDTO result = quizService.getQuizSetResult(quizSetId);
        return ResponseEntity.ok(result);
    }


}
