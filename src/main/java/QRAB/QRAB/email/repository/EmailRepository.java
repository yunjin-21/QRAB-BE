package QRAB.QRAB.email.repository;

import QRAB.QRAB.email.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findTopByToEmailOrderByCreatedAtDesc(String toEmail);
}
