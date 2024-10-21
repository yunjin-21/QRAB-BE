package QRAB.QRAB.quiz.service;

import QRAB.QRAB.quiz.dto.QuizGenerationRequestDTO;
import QRAB.QRAB.quiz.dto.QuizSetDTO;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizSet;
import QRAB.QRAB.chatgpt.service.ChatgptService;
import QRAB.QRAB.quiz.repository.QuizRepository;
import QRAB.QRAB.quiz.repository.QuizSetRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.repository.NoteRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizSetRepository quizSetRepository;
    private final ChatgptService chatgptService;
    private final UserService userService;
    private final NoteRepository noteRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, QuizSetRepository quizSetRepository, ChatgptService chatgptService, UserService userService, NoteRepository noteRepository) {
        this.quizRepository = quizRepository;
        this.quizSetRepository = quizSetRepository;
        this.chatgptService = chatgptService;
        this.userService = userService;
        this.noteRepository = noteRepository;
    }

    public QuizSetDTO createQuizSet(QuizGenerationRequestDTO requestDTO) {
        // 1. 사용자와 노트 객체 가져오기
        User user = userService.getUserById(requestDTO.getUserId());
        Note note = noteRepository.findById(requestDTO.getNoteId())
                .orElseThrow(() -> new RuntimeException("노트를 찾을 수 없습니다."));

        // 2. 퀴즈 세트 생성 및 저장
        QuizSet quizSet = new QuizSet();
        quizSet.setUser(user);
        quizSet.setNote(note);
        quizSet.setTotalQuestions(requestDTO.getTotalQuestions());
        quizSet.setStatus("unsolved");
        quizSet.setCreatedAt(LocalDateTime.now());
        quizSetRepository.save(quizSet);

        // 3. 사용자 전공 정보 가져오기
        Set<String> majorNames = user.getMajors().stream()
                .map(major -> major.getName())
                .collect(Collectors.toSet());

        String majorInfo = "주전공: " + majorNames.iterator().next(); // 첫 번째 전공을 주전공으로 가정
        List<String> additionalMajors = majorNames.stream().skip(1).collect(Collectors.toList());
        if (!additionalMajors.isEmpty()) {
            majorInfo += ", 복수전공1: " + additionalMajors.get(0);
        }
        if (additionalMajors.size() > 1) {
            majorInfo += ", 복수전공2: " + additionalMajors.get(1);
        }

        // 4. 노트 요약 내용 가져오기
        String noteSummary = note.getChatgptContent(); // 요약본을 가져옴

        // 5. 퀴즈 생성 프롬프트 작성
        String quizPrompt = String.format(
                "다음은 사용자가 풀어야 할 퀴즈를 생성하는 요청입니다. 퀴즈는 객관식 사지선다형이며, 각 퀴즈에 대해 난이도, 질문, 선택지, 정답, 풀이, 퀴즈 요약을 포함해 주세요. 총 %d개의 퀴즈를 생성해 주세요.\n\n" +
                        "사용자의 전공 정보는 다음과 같습니다:\n" +
                        "- %s\n\n" +
                        "난이도를 책정하는 기준은 다음과 같습니다:\n" +
                        "- easy: 주제에 대한 기본 개념을 묻고 있음. 10명 중 8명 이상의 정답자가 예상됨.\n" +
                        "- medium: 주제에 대한 심화 개념이나 더 깊은 이해를 요구함. 10명 중 5명 이하의 정답자가 예상됨.\n" +
                        "- hard: 주제에 대해 medium 난이도보다 더 깊은 이해를 요구함. 10명 중 3명 이하의 정답자가 예상됨.\n\n" +
                        "블로그 내용은 사용자의 전공과 관련될 수 있습니다. 이 정보를 바탕으로 퀴즈를 생성해 주세요.\n\n" +
                        "각 퀴즈의 형식은 JSON 형식으로 다음과 같이 작성해 주세요:\n" +
                        "{\n" +
                        "  \"difficulty\": \"난이도 (easy, medium, hard 중 하나)\",\n" +
                        "  \"question\": \"퀴즈 질문 내용\",\n" +
                        "  \"choices\": [\n" +
                        "    \"a. 선택지 1\",\n" +
                        "    \"b. 선택지 2\",\n" +
                        "    \"c. 선택지 3\",\n" +
                        "    \"d. 선택지 4\"\n" +
                        "  ],\n" +
                        "  \"correct_answer\": \"정답의 선택지 번호 (0부터 시작하여 0, 1, 2, 3 중 하나)\",\n" +
                        "  \"explanation\": \"정답 풀이\",\n" +
                        "  \"quiz_summary\": \"퀴즈 요약\"\n" +
                        "}\n" +
                        "정답의 선택지 번호는 0, 1, 2, 3이 고루 분포되어야 합니다.\n\n" +
                        "다음은 실제 사용자가 입력한 블로그 내용입니다. 반드시 이 블로그 내용에 관한 퀴즈를 %d개 출제하고, JSON 형식의 앞뒤에 아무 말도 덧붙이지 말고 JSON 형식으로만 반환해 주세요.:\n\n%s",
                requestDTO.getTotalQuestions(),
                majorInfo,
                requestDTO.getTotalQuestions(),
                noteSummary
        );

        // 6. GPT API 호출하여 퀴즈 생성
        List<Quiz> quizzes = chatgptService.generateQuiz(quizPrompt);

        // 7. 생성된 퀴즈를 데이터베이스에 저장
        for (Quiz quiz : quizzes) {
            quiz.setQuizSet(quizSet);
            quiz.setChoicesAsList(quiz.getChoicesAsList()); // JSON 문자열로 변환 후 저장
            quizRepository.save(quiz);
        }

        // 8. QuizSet DTO 생성하여 반환
        return new QuizSetDTO(quizSet);
    }

    /*
    // 퀴즈 파싱 및 저장 로직
    private List<Quiz> parseAndSaveQuizzes(String gptResponse, Long quizSetId) {
        List<Quiz> quizzes = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            quizzes = mapper.readValue(gptResponse, new TypeReference<List<Quiz>>(){});
            for(Quiz quiz : quizzes){
                quiz.setQuizSet(quizSetRepository.findById(quizSetId).orElseThrow(() -> new RuntimeException("퀴즈 세트가 존재하지 않습니다.")));
                quizRepository.save(quiz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quizzes;
    }
     */

    public List<Quiz> getQuizzesByQuizSetId(Long quizSetId){
        return quizRepository.findByQuizSet_QuizSetId(quizSetId);
    }
}
