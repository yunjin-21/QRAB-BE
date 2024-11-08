package QRAB.QRAB.record.domain;

import QRAB.QRAB.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor //기본 생성자를 자동 생성
@Table(name = "Record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;


    @Column(name = "login_date")
    private LocalDate loginDate; //로그인 날짜 저장

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Record(User user, LocalDate loginDate){
        this.user = user;
        this.loginDate = loginDate;
    }

}
