package QRAB.QRAB.quiz.service;

import QRAB.QRAB.quiz.domain.QuizResult;
import QRAB.QRAB.quiz.dto.QuizGenerationRequestDTO;
import QRAB.QRAB.quiz.dto.QuizResultDTO;
import QRAB.QRAB.quiz.dto.QuizSetDTO;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizSet;
import QRAB.QRAB.chatgpt.service.ChatgptService;
import QRAB.QRAB.quiz.dto.UnsolvedQuizSetResponseDTO;
import QRAB.QRAB.quiz.repository.QuizRepository;
import QRAB.QRAB.quiz.repository.QuizResultRepository;
import QRAB.QRAB.quiz.repository.QuizSetRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.repository.NoteRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizSetRepository quizSetRepository;
    private final ChatgptService chatgptService;
    private final NoteRepository noteRepository;
    private final QuizResultRepository quizResultRepository;

    @Autowired
    public QuizService(UserRepository userRepository, QuizRepository quizRepository, QuizSetRepository quizSetRepository,
                       ChatgptService chatgptService, NoteRepository noteRepository, QuizResultRepository quizResultRepository) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.quizSetRepository = quizSetRepository;
        this.chatgptService = chatgptService;
        this.noteRepository = noteRepository;
        this.quizResultRepository = quizResultRepository;
    }

    public QuizSetDTO createQuizSet(QuizGenerationRequestDTO requestDTO) {
        // 1. SecurityContext에서 인증된 사용자 이메일을 가져옴
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("Could not find user with email: " + username));

        // 2. 노트 조회
        Note note = noteRepository.findById(requestDTO.getNoteId())
                .orElseThrow(() -> new RuntimeException("노트를 찾을 수 없습니다."));

        // 3. 퀴즈 생성 시 노트의 퀴즈 생성 횟수 증가
        note.setQuizGenerationCount(note.getQuizGenerationCount() + 1);
        noteRepository.save(note);

        // 4. 퀴즈 세트 생성 및 저장
        QuizSet quizSet = new QuizSet();
        quizSet.setUser(user);
        quizSet.setNote(note);
        quizSet.setTotalQuestions(requestDTO.getTotalQuestions());
        quizSet.setStatus("unsolved");
        quizSet.setCreatedAt(LocalDateTime.now());
        quizSetRepository.save(quizSet);

        // 5. 사용자 전공 정보 가져오기
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

        // 5. 노트 요약 내용 가져오기
        String noteSummary = note.getChatgptContent(); // 요약본을 가져옴

        // 6. 퀴즈 생성 프롬프트 작성
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

        // 7. GPT API 호출하여 퀴즈 생성
        List<Quiz> quizzes = chatgptService.generateQuiz(quizPrompt);

        // 8. 생성된 퀴즈를 데이터베이스에 저장
        for (Quiz quiz : quizzes) {
            quiz.setQuizSet(quizSet);
            quiz.setChoicesAsList(quiz.getChoicesAsList()); // JSON 문자열로 변환 후 저장
            quizRepository.save(quiz);
        }

        // 9. QuizSet DTO 생성하여 반환
        return new QuizSetDTO(quizSet);
    }

    public List<Quiz> getQuizzesByQuizSetId(Long quizSetId){
        return quizRepository.findByQuizSet_QuizSetId(quizSetId);
    }

    public Page<QuizResultDTO> getSolvedQuizSets(int page) {
        Pageable pageable = PageRequest.of(page, 6); // 한 페이지에 6개의 퀴즈 세트
        Page<QuizResult> quizResults = quizResultRepository.findAllSolvedQuizSets(pageable);

        return quizResults.map(this::convertToDto);
    }

    //퀴즈 풀기 페이지 조회(unsolved quizset 조회)
    public Page<UnsolvedQuizSetResponseDTO> findUnsolvedQuizSets(int page) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("Could not find user with email: " + username));

        // 한 페이지에 6개씩 페이징
        Pageable pageable = PageRequest.of(page, 6);
        Page<QuizSet> quizSets = quizSetRepository.findByUserAndStatus(user, "unsolved", pageable);

        // 조회 결과 DTO로 변환하여 반환
        return quizSets.map(quizSet -> new UnsolvedQuizSetResponseDTO(
                quizSet.getQuizSetId(),
                quizSet.getNote().getId(),
                quizSet.getNote().getTitle(),
                quizSet.getCreatedAt(),
                "미풀이", // 아직 안 풀었으므로 풀이 일자 X
                quizSet.getStatus(),
                quizSet.getNote().getCategory().getName(),
                quizSet.getNote().getCategory().getParentCategory() != null
                        ? quizSet.getNote().getCategory().getParentCategory().getName() : null
        ));
    }

    private QuizResultDTO convertToDto(QuizResult quizResult) {
        QuizSet quizSet = quizResult.getQuizSet();
        Note note = quizSet.getNote();

        // 정답/오답 개수 계산
        int correctCount = quizResult.getCorrectCount();
        int totalQuestions = quizResult.getTotalQuestions();
        String answerSummary = (correctCount > totalQuestions / 2) ?
                "정답 " + correctCount + "문제" : "오답 " + (totalQuestions - correctCount) + "문제";

        // Note에서 카테고리 정보 가져오기
        String categoryName = note.getCategory().getName();
        String parentCategoryName = note.getCategory().getParentCategory() != null ? note.getCategory().getParentCategory().getName() : "";

        return new QuizResultDTO(
                note.getTitle(),
                totalQuestions,
                quizSet.getCreatedAt().toLocalDate().toString(),
                quizResult.getTakenAt().toLocalDate().toString(),
                answerSummary,
                categoryName,
                parentCategoryName
        );
    }


}
