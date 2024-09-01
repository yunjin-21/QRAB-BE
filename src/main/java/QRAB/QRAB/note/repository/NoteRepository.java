package QRAB.QRAB.note.repository;

import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.SummaryResponseDTO;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // db와 상호작용하는 클래스에 붙어 사용
public interface NoteRepository extends JpaRepository<Note, Long> {
    Page<Note> findByUser (User user, Pageable pageable);
}
