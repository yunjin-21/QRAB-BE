package QRAB.QRAB.login.domain;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.note.domain.Note;
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
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "email", length = 50, unique = true)
    private String username; //이메일을 아이디로 사용

    @Column(name = "real_name", length = 50, unique = true)
    private String realName; //사용자이름

    @Column(name = "nickname", length = 50, unique = true)
    private String nickname;// 닉네임

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;//전화번호

    @Column(name = "password", length = 100)
    private String password;//비밀번호

    @Column(name = "activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

   @OneToMany(mappedBy = "user")
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Category> categories = new ArrayList<>();

}
