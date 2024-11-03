package QRAB.QRAB.user.domain;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.friend.domain.Friendship;
import QRAB.QRAB.major.domain.Major;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USER")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "email", length = 50, unique = true)
    private String username; //이메일을 아이디로 사용

    @Column(name = "nickname", length = 50, unique = true)
    private String nickname;// 닉네임

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;//전화번호

    @Column(name = "password", length = 100)
    private String password;//비밀번호

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "notification")
    private int notification; //알림 설정 기본값 0 -> 비공개

    @ManyToMany
    @JoinTable(
            name = "user_authority",// 중간 테이블 user_authority 이름
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;// 권한들간 관계

    @ManyToMany
    @JoinTable(
            name = "user_major", // 중간 테이블 user_major 이름
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "major_id", referencedColumnName = "major_id")}
    )
    private Set<Major> majors; // 학과와의 관계

   @OneToMany(mappedBy = "user")
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Friendship> friends = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;


}
