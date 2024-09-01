package QRAB.QRAB.chatgpt.dto;

import QRAB.QRAB.chatgpt.domain.Chatgpt;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatgptRequestDTO {
    private String model;
    private List<Chatgpt> messages;
    private double temperature;
    private int max_tokens;
    private int top_p;
    private int frequency_penalty;
    private int presence_penalty;
    public ChatgptRequestDTO(String model, String content) {
        this.model = model; //gpt-4o-mini
        this.messages  =  new ArrayList<>();
        this.messages .add(new Chatgpt("system", "You are a helpful assistant.")); // system  + 메시지 추가
        this.messages .add(new Chatgpt("user", content)); //user + 메시지 추가
        this.temperature = 0.7;//퀴즈 생성에 제일 적합한 결과값이 도출되는 temperature 값
        this.max_tokens = 16000; // 최대 토큰
        this.top_p = 1;//모든 단어를 고려
        this.frequency_penalty = 0;
        this.presence_penalty = 0;

    }
}
