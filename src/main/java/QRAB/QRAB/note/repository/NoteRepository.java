package QRAB.QRAB.note.repository;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.SummaryResponseDTO;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // db와 상호작용하는 클래스에 붙어 사용
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser (User friend); //친구가 만든 노트 리스트 반환
    Page<Note> findByUser (User user, Pageable pageable);
    long countByUser(User user); // 유저에 따른 노트 개수 반환
    List<Note> findByUserOrderByCreatedAtDesc(User user);
    Page<Note> findByCategoryAndUser(Category category, User user, Pageable pageable);
}
