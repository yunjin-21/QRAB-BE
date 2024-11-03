package QRAB.QRAB.friend.repository;

import QRAB.QRAB.friend.domain.Friendship;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long>{
    boolean existsByUserAndFriend(User user, User friend); //Friendship 테이블에 user , friend 형태가 이미 존재하는지 확인
    List<Friendship> findByUser(User user);
}
