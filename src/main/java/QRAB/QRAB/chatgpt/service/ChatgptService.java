package QRAB.QRAB.chatgpt.service;

import QRAB.QRAB.chatgpt.dto.ChatgptRequestDTO;
import QRAB.QRAB.chatgpt.dto.ChatgptResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatgptService {
    private final RestTemplate restTemplate;
    @Value("${openai.api.key}")
    private String openAiKey;

    @Value("${openai.api.url}") // API 엔드포인트 URL을 가져오기
    private String apiUrl;
    @Value("${openai.model}")
    private String model;
    @Autowired
    public ChatgptService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String getSummary(String content){
        String prompt = content + "위 내용의 핵심 개념과 주요 정보를 포함하여, A4 용지 1페이지 분량으로 간결하고 정확하게 요약해줘. 중요한 통계나 데이터, 구체적인 예시가 있다면 꼭 포함하고, 불필요한 세부 사항은 제외해줘. 요약은 논리적인 흐름이 유지되도록 작성하고, 각 문단의 길이가 고르게 분포되도록 해줘.";// 요약 프롬프팅하기

        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);//DTO 요청 생성
        // postForObject(String url, Object request, Class<T> responseType)
        //ChatGPT API의 엔드포인트 URL + HTTP POST 요청의 본문(body)으로 전송되는 데이터 + 서버의 응답을 변환할 타입
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        return chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
    }

}
