package QRAB.QRAB.major.repository;

import QRAB.QRAB.major.domain.Major;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MajorRepository extends JpaRepository<Major, Long> {
    List<Major> findAllById(Iterable<Long> ids); //여러 아이디 Set<Long>를 통해서 전공 찾기
    List<Major> findAllByNameIn(List<String> names);
    boolean existsByName(String name); //학과 정보 존재하는지 여부

    List<Major> findByIdBetween(Long startId, Long endId); // 특정 범위에 있는 Major 들을 조회하도록
}
