package QRAB.QRAB.chatgpt.service;

import QRAB.QRAB.analysis.dto.DetailedAnalysisResponseDTO;
import QRAB.QRAB.chatgpt.dto.ChatgptRequestDTO;
import QRAB.QRAB.chatgpt.dto.ChatgptResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import QRAB.QRAB.quiz.domain.Quiz;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
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
        
       Summarize this content in a concise, organized format like the above examples. When you print it out, don't get # or *, just get the text. Focus on key concepts and maintain logical flow. Make it as detailed and comprehensive as possible, enough to fill three pages. Be generous of the amount of the text.
        """;

        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);

        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        return chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
    }
    // 퀴즈 생성, 응용 퀴즈 생성
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

    public String getResponse(String prompt) {
        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        return chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
    }

    public String generateDetailedAnalysis(String username, List<String> strongSummaries, List<String> weakSummaries,
                                           List<String> strongCategoryNames, List<String> weakCategoryNames) {
        String prompt = String.format(
                """
                다음은 %s 님의 강점 카테고리와 약점 카테고리에 대한 퀴즈 풀이 분석입니다:
        
                강점 카테고리: %s
                [강점 카테고리들의 잘 푼 문제들]
                %s

                약점 카테고리: %s
                [약점 카테고리들의 틀린 문제들]
                %s
                다음과 같은 형식으로 분석해주세요:
                '%s 님은 %s 카테고리에서 강점을 보이고 있어요. [강점 카테고리에서 잘 푼 내용] 관련 문제에서 정답률이 높네요. 특히, [구체적인 잘 푼 내용 나열]을 잘 이해하고 있어요.
                
                반면, %s에서는 어려움을 겪고 있는 것으로 보여요. [약점 카테고리에서 틀린 내용] 관련 문제에서 유달리 정답률이 낮네요. 특히, [구체적인 틀린 내용 나열] 부분에서 낮은 이해도를 보이고 있어요.'
                
                example:
                "sooshu 님은 파이썬과 코딩 카테고리에서 강점을 보이고 있어요.  VSCode와 Jupyter Notebook 파일 변환 관련 문제에서 정답률이 높네요. 특히, Jupyter Notebook 파일을 HTML로 변환하는 방법, nbconvert 패키지 설치 명령어, 파일 변환 시 사용할 수 있는 환경에 대한 내용을 잘 이해하고 있어요.
                
                반면, 자바와 컴퓨터네트워크에서는 어려움을 겪고 있는 것으로 보여요. TCP와 UDP 프로토콜, 그리고 커밋 메시지 작성 관련 주제에서 정답률이 낮네요. 특히, TCP와 UDP의 데이터 처리 방식이나 연결 설정 과정에 대한 심화 질문에서 낮은 이해도를 보이고 있는데, 이는 해당 주제에 대한 깊이 있는 지식이 부족함을 시사해요. 또한, 커밋 메시지의 구성 요소나 작성 원칙에 대한 기본적인 이해에서도 틀린 문제들이 많아, 이 부분에 대한 체계적인 학습이 필요해 보여요."
                
                주의사항:
                1. 강점 분석에서는 반드시 강점 카테고리의 내용만 언급하세요.
                2. 약점 분석에서는 반드시 약점 카테고리의 내용만 언급하세요.
                3. 분석은 구체적이고 명확하게 해주시고, 실제 문제 풀이 내용을 근거로 들어 설명해주세요.
                4. 특수문자나 마크다운 문법(-, *, `, #)은 사용하지 말고 순수 텍스트로만 작성해주세요.
                """,
                username,
                String.join("과 ", strongCategoryNames),
                String.join("\n", strongSummaries),
                String.join("과 ", weakCategoryNames),
                String.join("\n", weakSummaries),
                username,
                String.join("과 ", strongCategoryNames),
                String.join("과 ", weakCategoryNames)
        );

        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        return chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
    }

    public List<String> generateStudyTips(String categoryName, List<String> weakSummaries) {
        String prompt = String.format(
                """
                다음은 사용자가 %s 카테고리에서 틀린 문제들의 특징입니다:
                %s
        
                위 내용을 바탕으로, 이 카테고리의 실력 향상을 위한 구체적인 학습 방법 2-3개를 추천해주세요.
                각 방법은 다음과 같은 형식으로 작성해주세요:
                - [학습 내용]을 먼저 공부하세요. [학습 내용]은 [설명]입니다.
                - [구체적인 개념]을 복습하세요. [구체적인 개념]은 [설명]입니다.
                
                example:
                - TCP와 UDP의 기본 개념을 먼저 공부하세요. TCP는 연결 지향적이며 데이터 전송의 신뢰성을 보장하는 프로토콜입니다. UDP는 비연결 지향적이며 속도를 중시하는 프로토콜로, 데이터 전송의 신뢰성을 보장하지 않습니다.
                - 스택의 기본 개념인 LIFO 구조를 복습하세요. LIFO는 Last In First Out의 약자로, 가장 나중에 들어온 데이터가 가장 먼저 나가는 구조입니다. 스택의 pop(), push() 메소드와 같은 기본적인 작동 방식을 이해하는 것이 중요합니다.
                - 커밋 메시지의 기본 구성 요소를 공부하세요. 커밋 메시지는 제목, 본문, 꼬리말로 구성되며, 이를 통해 코드 변경 사항을 명확하게 설명하고 팀원들과의 소통을 원활하게 할 수 있습니다. 각 요소의 역할과 작성 원칙을 숙지하는 것이 좋습니다.
                
                주의사항:
                1. 학습 방법은 구체적이고 실천 가능한 내용으로 작성해주세요.
                2. 특수문자나 마크다운 문법(-, *, `, #)은 사용하지 말고 순수 텍스트로만 작성해주세요.
                3. JSON이나 특수문자 없이 순수 텍스트로만 작성해주세요.
                4. 각 팁은 새로운 줄에 작성하고 앞에 하이픈(-)을 붙여주세요.
                """,
                categoryName,
                String.join("\n", weakSummaries)
        );

        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        String response = chatgptResponseDTO != null ? chatgptResponseDTO.getFirstChoiceContent() : "";
        return Arrays.asList(response.split("\n")).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public List<DetailedAnalysisResponseDTO.ReferenceDTO> generateReferences(String categoryName, String content) {
        String prompt = String.format(
                """
                %s 분야의 %s 주제에 대해 추가 학습할 수 있도록 한국인 대학생에게 자료를 추천해주세요.
                먼저 온라인 학습이 가능한 링크 2개, 그 다음 관련 도서 링크 2개를 추천해주세요.
                다음과 같은 JSON 형식으로 작성해주세요:
                {
                  "references": [
                    {
                      "title": "문서/블로그/강좌/자료 제목",
                      "link": "URL"
                    }
                  ]
                }
                
                주의사항:
                1. 반드시 현재 접속 가능한 실제 웹사이트의 링크여야 합니다. 임의로 링크를 생성하지 마세요.
                2. 특수문자나 마크다운 문법(-, *, `, #)은 사용하지 말고 순수 텍스트로만 작성해주세요.
                """,
                categoryName,
                content
        );

        ChatgptRequestDTO chatgptRequestDTO = new ChatgptRequestDTO(model, prompt);
        ChatgptResponseDTO chatgptResponseDTO = restTemplate.postForObject(apiUrl, chatgptRequestDTO, ChatgptResponseDTO.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(chatgptResponseDTO.getFirstChoiceContent());
            List<DetailedAnalysisResponseDTO.ReferenceDTO> references = new ArrayList<>();

            if (root.has("references")) {
                root.get("references").forEach(reference -> {
                    DetailedAnalysisResponseDTO.ReferenceDTO dto = new DetailedAnalysisResponseDTO.ReferenceDTO();
                    dto.setTitle(reference.get("title").asText());
                    dto.setLink(reference.get("link").asText());
                    references.add(dto);
                });
            }

            return references;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse references", e);
        }
    }
    /*public List<DetailedAnalysisResponseDTO.ReferenceDTO> generateReferences(String categoryName, String content) {
        List<DetailedAnalysisResponseDTO.ReferenceDTO> references = new ArrayList<>();

        // 온라인 강좌 2개
        DetailedAnalysisResponseDTO.ReferenceDTO ref1 = new DetailedAnalysisResponseDTO.ReferenceDTO();
        ref1.setTitle("인프런 - " + categoryName + " 기초 강좌");
        ref1.setLink("https://www.inflearn.com/courses/" + categoryName.toLowerCase());
        references.add(ref1);

        DetailedAnalysisResponseDTO.ReferenceDTO ref2 = new DetailedAnalysisResponseDTO.ReferenceDTO();
        ref2.setTitle("YouTube - " + categoryName + " 학습 채널");
        ref2.setLink("https://www.youtube.com/results?search_query=" + categoryName.toLowerCase());
        references.add(ref2);

        // 도서 2개
        DetailedAnalysisResponseDTO.ReferenceDTO ref3 = new DetailedAnalysisResponseDTO.ReferenceDTO();
        ref3.setTitle(categoryName + " 기초 개념서");
        ref3.setLink("https://www.yes24.com/Product/Search?domain=ALL&query=" + categoryName);
        references.add(ref3);

        DetailedAnalysisResponseDTO.ReferenceDTO ref4 = new DetailedAnalysisResponseDTO.ReferenceDTO();
        ref4.setTitle(categoryName + " 심화 학습서");
        ref4.setLink("https://www.aladin.co.kr/search/wsearchresult.aspx?SearchWord=" + categoryName);
        references.add(ref4);

        return references;
    }*/

}
