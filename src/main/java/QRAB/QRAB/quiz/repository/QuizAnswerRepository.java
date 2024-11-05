package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.quiz.domain.QuizAnswer;
import QRAB.QRAB.quiz.domain.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findByQuizResult(QuizResult quizResult);
    // quizSetId로 오답 조회
    @Query("SELECT qa FROM QuizAnswer qa " +
            "JOIN qa.quiz q " +
            "JOIN q.quizSet qs " +
            "WHERE qs.quizSetId = :quizSetId AND qa.isCorrect = false")
    List<QuizAnswer> findIncorrectAnswersByQuizSetId(@Param("quizSetId") Long quizSetId);
}

