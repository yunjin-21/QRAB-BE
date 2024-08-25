package QRAB.QRAB.major.domain;

import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor //기본 생성자를 자동 생성
@Table(name = "MAJOR")
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본 키 값을 자동으로 생성
    @Column(name = "major_id")
    private Long id;

    @Column(name = "department", nullable = false)
    private String department; // 부서

    @Column(name = "name", nullable = false)
    private String name; //학과 이름

    @ManyToMany(mappedBy = "majors")
    private Set<User> users;

    public Major(String department, String name){
        this.department = department;
        this.name = name;
    }



}
