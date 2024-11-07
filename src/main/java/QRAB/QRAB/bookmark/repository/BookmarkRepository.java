package QRAB.QRAB.bookmark.repository;

import QRAB.QRAB.bookmark.domain.Bookmark;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUser(User user);
}
