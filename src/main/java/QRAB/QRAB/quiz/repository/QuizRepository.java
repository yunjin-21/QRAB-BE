package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByQuizSet_QuizSetId(Long quizSetId);
}
