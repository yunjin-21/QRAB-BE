package QRAB.QRAB.bookmark.repository;

import QRAB.QRAB.bookmark.domain.Bookmark;
import QRAB.QRAB.bookmark.dto.BookmarkedNoteResponseDTO;
import QRAB.QRAB.bookmark.dto.BookmarkedQuizResponseDTO;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.quiz.domain.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);

    @Query("SELECT new QRAB.QRAB.bookmark.dto.BookmarkedNoteResponseDTO(" +
            "n.id, n.title, COUNT(b.bookmarkId), n.quizGenerationCount) " +
            "FROM Bookmark b " +
            "JOIN b.quiz q " +
            "JOIN q.quizSet qs " +
            "JOIN qs.note n " +
            "WHERE b.user.userId = :userId " +
            "GROUP BY n.id, n.title, n.quizGenerationCount " +
            "HAVING COUNT(b.bookmarkId) > 0 " +
            "ORDER BY n.id")
    Page<BookmarkedNoteResponseDTO> findBookmarkedNotesWithCounts(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new QRAB.QRAB.bookmark.dto.BookmarkedQuizResponseDTO(" +
            "q.quizId, qs.quizSetId, q.question, q.choices, " +
            "qa.selectedAnswer, q.correctAnswer, qr.takenAt) " +
            "FROM Bookmark b " +
            "JOIN b.quiz q " +
            "JOIN q.quizSet qs " +
            "JOIN qs.note n " +
            "LEFT JOIN QuizAnswer qa ON qa.quiz.quizId = q.quizId AND qa.quizSet.quizSetId = qs.quizSetId " +
            "LEFT JOIN QuizResult qr ON qr.resultId = qa.quizResult.resultId " +
            "WHERE n.id = :noteId AND b.user.userId = :userId")
    List<BookmarkedQuizResponseDTO> findBookmarkedQuizzesByNoteId(@Param("noteId") Long noteId, @Param("userId") Long userId);
}
