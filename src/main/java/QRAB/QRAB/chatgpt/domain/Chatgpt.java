package QRAB.QRAB.chatgpt.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor //기본 생성자를 자동 생성
@Table(name = "CHATGPT")
public class Chatgpt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본 키 값을 자동으로 생성
    @Column(name = "chat_gpt_id")
    private Long id;
    private String role;
    private String content;
    public Chatgpt(String role, String content) {    // ID를 제외한 생성자 추가
        this.role = role;
        this.content = content;
    }


}