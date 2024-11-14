package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.quiz.domain.QuizResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    @Query("SELECT qr FROM QuizResult qr WHERE qr.quizSet.status = 'solved'")
    Page<QuizResult> findAllSolvedQuizSets(Pageable pageable);

    Optional<QuizResult> findByQuizSetQuizSetId(Long quizSetId);
}

