package QRAB.QRAB.quiz.dto;

import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.quiz.domain.QuizSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UnsolvedRecentQuizSetDTO {
    private Long quizSetId;
    private Long noteId;
    private String noteTitle;
    private String chatgptContent;
    private int totalQuestions;

    public static UnsolvedRecentQuizSetDTO fromEntity(QuizSet quizSet){
        return new UnsolvedRecentQuizSetDTO(
                quizSet.getQuizSetId(),
                quizSet.getNote().getId(),
                quizSet.getNote().getTitle(),
                quizSet.getNote().getChatgptContent().length() > 250 ? quizSet.getNote().getChatgptContent().substring(0, 250) : quizSet.getNote().getChatgptContent(),
                quizSet.getTotalQuestions()
        );

    }
}
