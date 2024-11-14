package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizSet;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {
    // status가 'unsolved'인 퀴즈세트만 조회
    Page<QuizSet> findByUserAndStatus(User user, String status, Pageable pageable);
    Page<QuizSet> findByNoteIdAndStatus(Long noteId, String status, Pageable pageable);
    List<QuizSet> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT qs FROM QuizSet qs WHERE qs.user = :user")
    List<QuizSet> findByUser(@Param("user") User user);

}