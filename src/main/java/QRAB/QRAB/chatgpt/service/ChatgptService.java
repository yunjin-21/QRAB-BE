package QRAB.QRAB.chatgpt.service;

import QRAB.QRAB.chatgpt.dto.ChatgptRequestDTO;
import QRAB.QRAB.chatgpt.dto.ChatgptResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import QRAB.QRAB.quiz.domain.Quiz;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

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
    // 퀴즈 생성
    public List<Quiz> generateQuiz(String quizPrompt) {
        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, quizPrompt);
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        if (chatgptResponseDTO != null && chatgptResponseDTO.getFirstChoiceContent() != null) {
            return parseQuizFromResponse(chatgptResponseDTO.getFirstChoiceContent());
        }
        return new ArrayList<>();
    }

    // GPT 응답을 Quiz 리스트로 변환
    private List<Quiz> parseQuizFromResponse(String content) {
        List<Quiz> quizzes = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> quizMaps = mapper.readValue(content, new TypeReference<List<Map<String, Object>>>(){});

            for (Map<String, Object> quizMap : quizMaps) {
                Quiz quiz = new Quiz();
                quiz.setDifficulty((String) quizMap.get("difficulty"));
                quiz.setQuestion((String) quizMap.get("question"));

                // choices 필드를 문자열 리스트로 변환
                List<String> choices = ((List<?>) quizMap.get("choices")).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
                quiz.setChoicesAsList(choices);

                // correct_answer 필드 처리
                // Integer 또는 String일 가능성을 모두 고려하여 처리
                Object correctAnswerObj = quizMap.get("correct_answer");
                int correctAnswer;
                if (correctAnswerObj instanceof Integer) {
                    correctAnswer = (Integer) correctAnswerObj;
                } else if (correctAnswerObj instanceof String) {
                    correctAnswer = Integer.parseInt((String) correctAnswerObj);
                } else {
                    throw new IllegalArgumentException("Invalid correct_answer format: " + correctAnswerObj);
                }
                quiz.setCorrectAnswer(correctAnswer);

                quiz.setExplanation((String) quizMap.get("explanation"));
                quiz.setQuizSummary((String) quizMap.get("quiz_summary"));

                quizzes.add(quiz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quizzes;
    }
}
