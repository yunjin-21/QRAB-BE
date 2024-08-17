package QRAB.QRAB.login.repository;

import QRAB.QRAB.login.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}