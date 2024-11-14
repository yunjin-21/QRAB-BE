package QRAB.QRAB.email.domain;

import QRAB.QRAB.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor //기본 생성자를 자동 생성
@Table(name = "EMAIL")
public class Email extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private Long id;
    private String toEmail;
    private int hour; // 1~12
    private int minute; // 0~59
    private String ampm;
    private LocalDateTime scheduledTime;

    @Builder
    public Email(String toEmail, int hour, int minute, String ampm, LocalDateTime scheduledTime) {
        this.toEmail = toEmail;
        this.hour = hour;
        this.minute = minute;
        this.ampm = ampm;
        this.scheduledTime = scheduledTime;
    }
}
