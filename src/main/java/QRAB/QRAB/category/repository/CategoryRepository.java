package QRAB.QRAB.category.repository;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    //특정 상위 카테고리의 하위 카테고리 조회
    List<Category> findByParentCategory(Category parentCategory); //부모 카테고리를 기준으로 자식 카테고리들 조회

    @Query("SELECT c FROM Category c ORDER BY c.createdAt ASC")
    List<Category> findAllOrderByCreatedAt();

    @Query("SELECT c FROM Category c WHERE c.parentCategory is NULL")
    List<Category> findTopLevelCategories(); // parent id 가 null 인 모든 카테고리 조회
    boolean existsByName(String name); //카테고리 이름으로 존재 여부 확인
    boolean existsByNameAndUser(String name, User user); // 카테고리 이름과 사용자 기준으로 중복 여부 확인

    boolean existsByNameAndIdNot(String name, Long id); // 주어진 name 값을 가진 객체가 존재하는지 확인 + 해당 id 값과 다른 객체만 대상으로 중복 여부 확인

    boolean existsByNameAndUserAndIdNot(String name, User user, Long categoryId);// 같은 사용자 내에서 같은 카테고리 ID가 있는지
    List<Category> findByUser(User user); // 특정 사용자가 생성한 카테고리 조회

}
