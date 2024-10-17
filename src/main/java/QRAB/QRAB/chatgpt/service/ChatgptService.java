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
        // Few-Shot Prompting 예시
        String fewShotExamples = """
        Here are examples of how to summarize technical content in the desired format:

        Example 1:
        컴퓨터 구조 개요
        컴퓨터는 하드웨어와 소프트웨어로 구성된다. 하드웨어는 CPU, RAM, ROM, SSD, HDD와 같은 저장 장치, 그리고 입출력 장치로 나뉜다. CPU는 컴퓨터의 두뇌 역할을 하며, RAM은 휘발성 메모리로 데이터를 임시 저장하고, ROM은 부팅에 필요한 데이터를 저장하는 비휘발성 메모리다. 소프트웨어는 운영체제와 응용 소프트웨어로 나뉜다.

        Example 2:
        데이터 표현
        컴퓨터에서 데이터는 수치와 비수치 데이터로 구분된다. 보수 개념은 덧셈을 통해 뺄셈을 수행하는데, 2의 보수는 1의 보수를 구한 뒤 +1을 더해 음수를 쉽게 계산한다. 10진수와 2진수 변환 시 unpacked decimal과 packed decimal 변환이 필요하다.

        Now, summarize the following content in a similar style, but this time, make it as detailed and comprehensive as possible to fill an A4 page. The summary should be thorough, ensuring all key details and examples are covered while maintaining a logical structure and flow. 
        """;

        // 사용자 입력 데이터와 결합된 프롬프트 생성
        String prompt = fewShotExamples + "\n" + content +
                "\n\nSummarize this content in a concise, organized format like the above examples. Focus on key concepts and maintain logical flow.";

        // ChatgptRequestDTO 생성
        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);

        // API 요청 및 응답 처리
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        return chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
    }
}
