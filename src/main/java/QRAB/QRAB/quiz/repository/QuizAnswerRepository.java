package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.quiz.domain.QuizAnswer;
import QRAB.QRAB.quiz.domain.QuizResult;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findByQuizResult(QuizResult quizResult);
    List<QuizAnswer> findByQuiz_QuizIdAndIsCorrectFalse(Note note);
    // quizSetId로 오답 조회
    @Query("SELECT qa FROM QuizAnswer qa " +
            "JOIN qa.quiz q " +
            "JOIN q.quizSet qs " +
            "WHERE qs.quizSetId = :quizSetId AND qa.isCorrect = false")
    List<QuizAnswer> findIncorrectAnswersByQuizSetId(@Param("quizSetId") Long quizSetId);

    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.quiz.note.id = :noteId AND qa.isCorrect = false")
    List<QuizAnswer> findIncorrectAnswersByNoteId(@Param("noteId") Long noteId);

    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.quiz.quizId = :quizId AND qa.isCorrect = false")
    Optional<QuizAnswer> findByQuizIdAndIsCorrectFalse(@Param("quizId") Long quizId);


    // 최근 틀린 퀴즈 조회
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.isCorrect = false ORDER BY qa.quizSet.createdAt DESC")
    List<QuizAnswer> findRecentWrongAnswers();



    /*@Query("SELECT qa FROM QuizAnswer qa JOIN qa.quiz q JOIN q.quizSet qs WHERE qs.status = :status AND qs.user = :user AND qa.isCorrect = false ORDER BY q.createdAt DESC")
    List<QuizAnswer> findTop3ByQuizSetStatusAndUserAndIsCorrectFalseOrderByQuizCreatedAtDesc(@Param("status") String status, @Param("user") User user);*/
}

