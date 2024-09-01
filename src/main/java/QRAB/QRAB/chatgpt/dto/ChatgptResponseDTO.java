package QRAB.QRAB.chatgpt.dto;

import QRAB.QRAB.chatgpt.domain.Chatgpt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatgptResponseDTO {
    private List<Choice> choices; // 여러 선택지를 담는 리스트

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;       // 선택지의 인덱스
        private Chatgpt message; // GPT 의 응답 메시지 (role 과 content)를 포함
    }

    // 첫 번째 선택지의 content 를 가져오는 메서드
    public String getFirstChoiceContent() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).message.getContent();
        }
        return null;
    }
}