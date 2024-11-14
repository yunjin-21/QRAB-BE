package QRAB.QRAB.quiz.service;

import QRAB.QRAB.quiz.domain.QuizAnswer;
import QRAB.QRAB.quiz.domain.QuizResult;
import QRAB.QRAB.quiz.dto.*;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.domain.QuizSet;
import QRAB.QRAB.quiz.dto.QuizRegenerationRequestDTO;
import QRAB.QRAB.chatgpt.service.ChatgptService;
import QRAB.QRAB.quiz.repository.QuizAnswerRepository;
import QRAB.QRAB.quiz.repository.QuizRepository;
import QRAB.QRAB.quiz.repository.QuizResultRepository;
import QRAB.QRAB.quiz.repository.QuizSetRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.repository.NoteRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.user.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizSetRepository quizSetRepository;
    private final ChatgptService chatgptService;
    private final NoteRepository noteRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    @Autowired
    public QuizService(UserRepository userRepository, QuizRepository quizRepository, QuizSetRepository quizSetRepository,
                       ChatgptService chatgptService, NoteRepository noteRepository, QuizResultRepository quizResultRepository,
                       QuizAnswerRepository quizAnswerRepository) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.quizSetRepository = quizSetRepository;
        this.chatgptService = chatgptService;
        this.noteRepository = noteRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizAnswerRepository = quizAnswerRepository;
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
                        "다음은 실제 사용자가 입력한 블로그 내용입니다. 반드시 이 블로그 내용에 관한 퀴즈를 %d개 출제하고, JSON 형식의 앞뒤에 아무 말도 덧붙이지 말고 JSON 형식으로만 반환해 주세요.\n\n" +
                        "%s\n\n" +
                        "블로그 내용이 끝났습니다. 가장 중요한 것은 **앞뒤에 아무 말도 없이** **JSON 형식**으로 **%d개의 퀴즈**를 출제하는 것입니다.",
                requestDTO.getTotalQuestions(),
                majorInfo,
                requestDTO.getTotalQuestions(),
                noteSummary,
                requestDTO.getTotalQuestions()

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

    public QuizGradingResponseDTO getQuizSetResult(Long quizSetId) {
        // QuizResult를 quizSetId를 기준으로 조회
        QuizResult quizResult = quizResultRepository.findByQuizSetQuizSetId(quizSetId)
                .orElseThrow(() -> new RuntimeException("Quiz result not found"));

        // QuizAnswer 목록을 가져와 QuizResultDetailDTO 리스트 생성
        List<QuizAnswer> quizAnswers = quizAnswerRepository.findByQuizResult(quizResult);
        List<QuizGradingResponseDTO.QuizResultDetailDTO> quizResultDetails = new ArrayList<>();

        for (QuizAnswer answer : quizAnswers) {
            Quiz quiz = answer.getQuiz();
            QuizGradingResponseDTO.QuizResultDetailDTO detail = new QuizGradingResponseDTO.QuizResultDetailDTO();
            detail.setQuizId(quiz.getQuizId());
            detail.setDifficulty(quiz.getDifficulty());
            detail.setQuestion(quiz.getQuestion());
            detail.setChoices(quiz.getChoicesAsList());
            detail.setSelectedAnswer(answer.getSelectedAnswer());
            detail.setCorrectAnswer(quiz.getCorrectAnswer());
            detail.setExplanation(quiz.getExplanation());
            detail.setIsCorrect(answer.isCorrect());
            quizResultDetails.add(detail);
        }

        // QuizGradingResponseDTO 생성
        QuizGradingResponseDTO response = new QuizGradingResponseDTO();
        response.setQuizSetId(quizSetId); // 입력받은 quizSetId를 그대로 설정
        response.setNoteTitle(quizResult.getQuizSet().getNote().getTitle());
        response.setScore(quizResult.getScore());
        response.setCorrectCount(quizResult.getCorrectCount());
        response.setTotalQuestions(quizResult.getTotalQuestions());
        response.setTakenAt(quizResult.getTakenAt());
        response.setQuizzes(quizResultDetails);

        return response;
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

    public Page<QuizResultDTO> getSolvedQuizSetsByNoteId(Long noteId, int page) {
        Pageable pageable = PageRequest.of(page, 6); // 페이지 당 6개로 설정
        return quizSetRepository.findByNoteIdAndStatus(noteId, "solved", pageable)
                .map(quizSet -> {
                    // quizSetId로 QuizResult 조회 (Optional 처리 포함)
                    QuizResult quizResult = quizResultRepository.findByQuizSetQuizSetId(quizSet.getQuizSetId())
                            .orElseThrow(() -> new EntityNotFoundException("QuizResult not found for quizSetId: " + quizSet.getQuizSetId()));
                    return new QuizResultDTO(quizSet, quizResult);
                });
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

    // 특정 노트 unsolved 퀴즈 세트 조회
    public Map<String, Object> findUnsolvedQuizSetsByNoteId(Long noteId, int page) {
        // Note 조회 및 제목 가져오기
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));
        String noteTitle = note.getTitle();

        Pageable pageable = PageRequest.of(page, 6); // 한 페이지에 6개
        Page<QuizSetDTO> quizSetDTOs = quizSetRepository.findByNoteIdAndStatus(noteId, "unsolved", pageable)
                .map(quizSet -> new QuizSetDTO(
                        quizSet.getQuizSetId(),
                        quizSet.getNote() != null ? quizSet.getNote().getId() : null,
                        quizSet.getUser() != null ? quizSet.getUser().getUserId() : null,
                        quizSet.getTotalQuestions(),
                        quizSet.getCreatedAt(),
                        quizSet.getStatus(),
                        quizSet.getAccuracyRate()
                ));

        // 최상위 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("noteTitle", noteTitle); // 최상위 noteTitle 추가
        response.put("content", quizSetDTOs.getContent());
        response.put("totalElements", quizSetDTOs.getTotalElements());
        response.put("totalPages", quizSetDTOs.getTotalPages());
        response.put("size", quizSetDTOs.getSize());
        response.put("number", quizSetDTOs.getNumber());
        response.put("first", quizSetDTOs.isFirst());
        response.put("last", quizSetDTOs.isLast());
        response.put("numberOfElements", quizSetDTOs.getNumberOfElements());

        return response;
    }

    // 오답 복습 퀴즈 조회
    public ReviewWrongQuizDTO getReviewWrongQuizzesByQuizSetId(Long quizSetId) {
        QuizSet quizSet = quizSetRepository.findById(quizSetId)
                .orElseThrow(() -> new EntityNotFoundException("QuizSet not found with id: " + quizSetId));

        // 'quizSetId'에 속한 오답 퀴즈만 조회
        List<Quiz> incorrectQuizzes = quizAnswerRepository.findIncorrectAnswersByQuizSetId(quizSetId).stream()
                .map(QuizAnswer::getQuiz)
                .distinct() // 중복 퀴즈 제거
                .collect(Collectors.toList());

        // ReviewWrongQuizDTO 생성
        List<ReviewWrongQuizDTO.QuizDetail> quizDetails = incorrectQuizzes.stream()
                .map(quiz -> new ReviewWrongQuizDTO.QuizDetail(
                        quiz.getQuizId(),
                        quiz.getQuizSet().getQuizSetId(),
                        quiz.getQuestion(),
                        quiz.getChoicesAsList(),
                        quiz.getDifficulty()
                ))
                .collect(Collectors.toList());

        return new ReviewWrongQuizDTO(quizSet.getNote().getTitle(), quizDetails);
    }

    // 오답 복습 퀴즈 채점
    public QuizGradingResponseDTO gradeReviewWrongQuiz(Long quizSetId, QuizGradingRequestDTO requestDTO) {
        QuizSet quizSet = quizSetRepository.findById(quizSetId)
                .orElseThrow(() -> new EntityNotFoundException("QuizSet not found with id: " + quizSetId));

        String noteTitle = quizSet.getNote().getTitle();
        List<QuizGradingResponseDTO.QuizResultDetailDTO> quizResultDetails = new ArrayList<>();

        int correctCount = 0;

        for (QuizGradingRequestDTO.AnswerDTO answer : requestDTO.getAnswers()) {
            Quiz quiz = quizRepository.findById(answer.getQuizId())
                    .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + answer.getQuizId()));

            boolean isCorrect = (quiz.getCorrectAnswer() == answer.getSelectedAnswer());
            if (isCorrect) {
                correctCount++;
            }

            // QuizResultDetailDTO 객체를 생성하고 값 설정
            QuizGradingResponseDTO.QuizResultDetailDTO detailDTO = new QuizGradingResponseDTO.QuizResultDetailDTO();
            detailDTO.setQuizId(quiz.getQuizId());
            detailDTO.setDifficulty(quiz.getDifficulty());
            detailDTO.setQuestion(quiz.getQuestion());
            detailDTO.setChoices(quiz.getChoicesAsList());
            detailDTO.setSelectedAnswer(answer.getSelectedAnswer());
            detailDTO.setCorrectAnswer(quiz.getCorrectAnswer());
            detailDTO.setExplanation(quiz.getExplanation());
            detailDTO.setIsCorrect(isCorrect); // isCorrect 필드 설정

            quizResultDetails.add(detailDTO);
        }

        int totalQuestions = requestDTO.getAnswers().size();
        int score = (int) ((correctCount / (double) totalQuestions) * 100);

        // QuizGradingResponseDTO 객체를 생성하고 값 설정
        QuizGradingResponseDTO responseDTO = new QuizGradingResponseDTO();
        responseDTO.setNoteTitle(noteTitle);
        responseDTO.setScore(score);
        responseDTO.setCorrectCount(correctCount);
        responseDTO.setTotalQuestions(totalQuestions);
        responseDTO.setTakenAt(LocalDateTime.now());
        responseDTO.setQuizzes(quizResultDetails);

        return responseDTO;
    }

    // 최근 틀린 퀴즈 3개 조회
    public List<RecentWrongQuizDTO> getRecentWrongQuizzes() {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("No authenticated user found"));
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Pageable pageable = PageRequest.of(0, 3);
        return quizRepository.findRecentWrongQuizzesByUserId(user.getUserId(), pageable).getContent();

        List<QuizAnswer> recentWrongAnswers = quizAnswerRepository.findRecentWrongAnswers();
        return recentWrongAnswers.stream()
                .limit(3) // 최근 틀린 퀴즈 3개만 반환
                .map(answer -> new RecentWrongQuizDTO(
                        answer.getQuiz().getQuizId(),
                        answer.getQuiz().getQuestion(),
                        answer.getQuiz().getChoicesAsList(),
                        answer.getSelectedAnswer(),
                        answer.getQuiz().getCorrectAnswer(),
                        answer.isCorrect()
                ))
                .collect(Collectors.toList());
    }
  
    @Transactional(readOnly = true)
    public List<UnsolvedRecentQuizSetDTO> getRecentUnsolvedQuizSets(String username){
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));
        List<QuizSet> quizSets = quizSetRepository.findByUserOrderByCreatedAtDesc(user);
        List<UnsolvedRecentQuizSetDTO> unsolvedRecentQuizSetDTOS = quizSets.stream()
                .limit(3).map(UnsolvedRecentQuizSetDTO::fromEntity).toList();
        return unsolvedRecentQuizSetDTOS;
    }

    public QuizSetDTO regenerateQuiz(QuizRegenerationRequestDTO request) {
        if ("new".equals(request.getQuizType())) {
            // "new" 타입인 경우 기존 createQuizSet 호출
            QuizGenerationRequestDTO generationRequestDTO = new QuizGenerationRequestDTO();
            generationRequestDTO.setNoteId(request.getNoteId());
            generationRequestDTO.setTotalQuestions(request.getTotalQuestions());
            return createQuizSet(generationRequestDTO);
        }

        // "review" 타입인 경우 기존 로직 실행
        Note note = noteRepository.findById(request.getNoteId())
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + request.getNoteId()));

        // 틀린 문제의 QuizSummary를 수집하여 프롬프트 생성
        List<String> quizSummaries = quizAnswerRepository.findIncorrectAnswersByNoteId(request.getNoteId())
                .stream()
                .map(QuizAnswer::getQuiz) // QuizAnswer에서 Quiz 객체 가져오기
                .map(Quiz::getQuizSummary) // Quiz 객체에서 QuizSummary 추출
                .collect(Collectors.toList());

        String regenQuizPrompt = String.format(
                "다음은 사용자가 이전에 틀린 문제와 유사한 주제의 문제를 생성하는 요청입니다. 퀴즈는 객관식 사지선다형이며, 각 퀴즈에 대해 난이도, 질문, 선택지, 정답, 풀이, 퀴즈 요약을 포함해 주세요. 총 %d개의 퀴즈를 생성해 주세요.\n\n" +
                        "해당 노트의 주제는 다음과 같습니다:\n\n" +
                        "- 노트 제목: %s\n" +
                        "난이도를 책정하는 기준은 다음과 같습니다:\n" +
                        "- easy: 주제에 대한 기본 개념을 묻고 있음. 10명 중 8명 이상의 정답자가 예상됨.\n" +
                        "- medium: 주제에 대한 심화 개념이나 더 깊은 이해를 요구함. 10명 중 5명 이하의 정답자가 예상됨.\n" +
                        "- hard: 주제에 대해 medium 난이도보다 더 깊은 이해를 요구함. 10명 중 3명 이하의 정답자가 예상됨.\n\n" +
                        "이전 틀린 문제들의 내용을 바탕으로 유사한 퀴즈를 생성해 주세요.\n\n" +
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
                        "다음은 이전에 틀렸던 퀴즈의 내용 요약입니다. 반드시 이 내용에 관한 퀴즈를 %d개 출제하고, JSON 형식의 앞뒤에 아무 말도 덧붙이지 말고 JSON 형식으로만 반환해 주세요.\n\n" +
                        "%s\n\n" +
                        "내용 요약이 끝났습니다. 가장 중요한 것은 **꼭 틀린 문제와 유사한 주제, 내용으로** **앞뒤에 아무 말도 없이** **JSON 형식**으로 **%d개의 퀴즈**를 출제하는 것입니다.",
                request.getTotalQuestions(),
                note.getTitle(),
                request.getTotalQuestions(),
                String.join("\n", quizSummaries),
                request.getTotalQuestions()
        );

        // GPT 호출하여 퀴즈 생성
        List<Quiz> quizzes = chatgptService.generateQuiz(regenQuizPrompt);

        // QuizSet 생성 및 저장
        QuizSet quizSet = new QuizSet();
        quizSet.setNote(note);
        quizSet.setUser(note.getUser());
        quizSet.setTotalQuestions(request.getTotalQuestions());
        quizSet.setCreatedAt(LocalDateTime.now());
        quizSet.setStatus("unsolved");
        quizSetRepository.save(quizSet);

        // 생성된 퀴즈를 데이터베이스에 저장
        for (Quiz quiz : quizzes) {
            quiz.setQuizSet(quizSet);
            quizRepository.save(quiz);
        }

        return new QuizSetDTO(quizSet);
    }
}
