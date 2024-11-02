package QRAB.QRAB.user.repository;


import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "authorities")//Lazy조회가 아닌 Eager조회로 authorities정보를 같이 가져옴
    Optional<User> findOneWithAuthoritiesByUsername(String username);// 이메일로 사용자 조회
    Optional<User> findByNickname(String nickname);//친구 추가할때 닉네임으로 친구가 있는지 확인
    Optional<User> findByUsername(String username); //User 엔티티의 필드 username이 메서드에 쓰이는 것

}