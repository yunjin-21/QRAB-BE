package QRAB.QRAB.note.repository;

import QRAB.QRAB.analysis.dto.CategoryQuizGenerationDTO;
import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.SummaryResponseDTO;
import QRAB.QRAB.analysis.dto.WeakCategoryResponseDTO;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // db와 상호작용하는 클래스에 붙어 사용
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser (User friend); //친구가 만든 노트 리스트 반환
    Page<Note> findByUser (User user, Pageable pageable);
    long countByUser(User user); // 유저에 따른 노트 개수 반환
    List<Note> findByUserOrderByCreatedAtDesc(User user);
    Page<Note> findByCategoryAndUser(Category category, User user, Pageable pageable);

    // 다수의 Category를 받는 새 메서드
    @Query("SELECT n FROM Note n WHERE n.category IN :categories AND n.user = :user")
    Page<Note> findByCategoriesAndUser(@Param("categories") List<Category> categories, @Param("user") User user, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.user = :user")
    List<Note> findNotesByUser(@Param("user") User user);

    @Query("SELECT new QRAB.QRAB.analysis.dto.CategoryQuizGenerationDTO(" +
            "c.name, CAST(SUM(n.quizGenerationCount) AS int)) " +
            "FROM Note n " +
            "JOIN n.category c " +
            "WHERE n.user = :user " +
            "GROUP BY c.name")
    List<CategoryQuizGenerationDTO> findQuizGenerationCountPerCategory(@Param("user") User user);
}
