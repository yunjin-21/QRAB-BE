package QRAB.QRAB.quiz.service;

import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizSet;
import QRAB.QRAB.quiz.dto.QuizSolvingResponseDTO;
import QRAB.QRAB.quiz.repository.QuizRepository;
import QRAB.QRAB.quiz.repository.QuizSetRepository;
import QRAB.QRAB.note.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizSolvingService {

    private final QuizRepository quizRepository;
    private final QuizSetRepository quizSetRepository;

    @Autowired
    public QuizSolvingService(QuizRepository quizRepository, QuizSetRepository quizSetRepository) {
        this.quizRepository = quizRepository;
        this.quizSetRepository = quizSetRepository;
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
}
