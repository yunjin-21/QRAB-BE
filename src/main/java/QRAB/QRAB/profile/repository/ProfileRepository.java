package QRAB.QRAB.profile.repository;

import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
