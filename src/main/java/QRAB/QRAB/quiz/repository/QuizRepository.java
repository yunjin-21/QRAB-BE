package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizAnswer;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByQuizSet_QuizSetId(Long quizSetId);


    @Query("SELECT DISTINCT q FROM Quiz q JOIN q.quizAnswers qa WHERE q.note.id = :noteId AND qa.isCorrect = false")
    List<Quiz> findQuizzesWithIncorrectAnswers(@Param("noteId") Long noteId);
}
