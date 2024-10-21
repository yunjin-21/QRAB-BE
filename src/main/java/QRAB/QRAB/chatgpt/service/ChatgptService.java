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

    /*public String getSummary(String content){
        String prompt = content + "위 내용의 핵심 개념과 주요 정보를 포함하여, A4 용지 1페이지 분량으로 간결하고 정확하게 요약해줘. 중요한 통계나 데이터, 구체적인 예시가 있다면 꼭 포함하고, 불필요한 세부 사항은 제외해줘. 요약은 논리적인 흐름이 유지되도록 작성하고, 각 문단의 길이가 고르게 분포되도록 해줘.";// 요약 프롬프팅하기

        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);//DTO 요청 생성
        // postForObject(String url, Object request, Class<T> responseType)
        //ChatGPT API의 엔드포인트 URL + HTTP POST 요청의 본문(body)으로 전송되는 데이터 + 서버의 응답을 변환할 타입
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        return chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
    }

}*/
    public String getSummary(String content) {
        String fewShotExamples = """
        Here are examples of how to summarize AI-related technical content in the desired format:

        Example 1:
        머신러닝 개요
        머신러닝은 데이터를 기반으로 학습하여 예측하는 알고리즘이다. 지도 학습과 비지도 학습으로 나뉘며, 지도 학습은 라벨이 있는 데이터를 사용하고, 비지도 학습은 라벨이 없는 데이터를 클러스터링하는 방식이다. 주요 알고리즘으로는 선형 회귀, 로지스틱 회귀, K-최근접 이웃(KNN), K-평균(K-means) 등이 있다. 이외에도 딥러닝은 신경망을 사용해 복잡한 패턴을 학습하는 방법이다.

        Example 2:
        자연어 처리(NLP)
        자연어 처리는 컴퓨터가 인간의 언어를 이해하고 생성하는 기술이다. 주요 과제로는 텍스트 분류, 감정 분석, 기계 번역 등이 있으며, BERT와 GPT와 같은 사전 훈련된 언어 모델이 NLP에서 자주 사용된다. 텍스트 데이터의 전처리 과정은 토큰화, 정규화, 불용어 제거 등이 포함되며, 이 과정을 통해 모델의 성능을 높일 수 있다.

        Now, summarize the following content in a similar style, making it clear and detailed. The summary should cover key concepts, provide specific examples, and maintain a logical flow throughout. Focus on key concepts and maintain logical flow.
        """;

        String prompt = fewShotExamples + "\n\n" + content + """
        
        Summarize this content in a concise, organized format like the above examples. When you print it out, don't get # or *, just get the text. Focus on key concepts and maintain logical flow. Make it as detailed and comprehensive as possible, enough to fill an A4 page.
        """;

        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);

        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        return chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
    }

}
