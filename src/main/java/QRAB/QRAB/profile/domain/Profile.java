package QRAB.QRAB.profile.domain;

import QRAB.QRAB.major.domain.Major;
import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor //기본 생성자를 자동 생성
@Table(name = "Profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user; // 유저와 1대 1 매핑
    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "nickname", length = 50, unique = true)
    private String nickname;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;//전화번호


    //알림 설정 필드

    @Builder
    public Profile(User user, String imgUrl, String nickname, String email, String password, String phoneNumber){
        this.user = user;
        this.imgUrl = imgUrl;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
