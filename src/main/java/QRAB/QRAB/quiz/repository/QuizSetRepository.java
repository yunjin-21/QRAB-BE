package QRAB.QRAB.quiz.repository;

import QRAB.QRAB.quiz.domain.QuizSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {
}