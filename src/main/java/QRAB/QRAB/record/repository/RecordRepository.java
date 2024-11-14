package QRAB.QRAB.record.repository;

import QRAB.QRAB.record.domain.Record;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByUserAndLoginDate(User user, LocalDate loginDate);
}
