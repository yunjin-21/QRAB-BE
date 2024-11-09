package QRAB.QRAB.quiz.dto;

import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizAnswer;
import QRAB.QRAB.quiz.domain.QuizSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentWrongQuizDTO {
    private Long quizId;
    private String question; //질문
    private List<String> choices; //4개의 문장
    private int selectedAnswer;//사용자가 선택한 답
    private int correctAnswer;//정답


    public static RecentWrongQuizDTO fromEntity(QuizAnswer quizAnswer){
        Quiz quiz = quizAnswer.getQuiz();
        return new RecentWrongQuizDTO(
                quiz.getQuizId(),
                quiz.getQuestion(),
                quiz.getChoicesAsList(),
                quizAnswer.getSelectedAnswer(),
                quiz.getCorrectAnswer()
        );
    }
}

