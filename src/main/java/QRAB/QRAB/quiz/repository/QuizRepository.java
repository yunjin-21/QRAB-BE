package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.dto.RecentWrongQuizDTO;
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

    @Query("SELECT new QRAB.QRAB.quiz.dto.RecentWrongQuizDTO(" +
            "q.quizId, q.question, q.choices, qa.selectedAnswer, q.correctAnswer, qr.takenAt) " +
            "FROM QuizAnswer qa " +
            "JOIN qa.quiz q " +
            "JOIN qa.quizResult qr " +
            "WHERE qr.user.userId = :userId " +
            "AND qa.isCorrect = false " +
            "ORDER BY qr.takenAt DESC")
    Page<RecentWrongQuizDTO> findRecentWrongQuizzesByUserId(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT CASE WHEN COUNT(qs) > 0 THEN true ELSE false END " +
            "FROM QuizSet qs " +
            "WHERE qs.note.id = :noteId AND qs.status = :status")
    boolean existsByNoteAndQuizSet_Status(@Param("noteId") Long noteId,
                                          @Param("status") String status);

    @Query("SELECT DISTINCT q FROM Quiz q JOIN q.quizAnswers qa WHERE q.note.id = :noteId AND qa.isCorrect = false")
    List<Quiz> findQuizzesWithIncorrectAnswers(@Param("noteId") Long noteId);
}
