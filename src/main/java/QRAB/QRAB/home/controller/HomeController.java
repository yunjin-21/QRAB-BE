package QRAB.QRAB.home.controller;

import QRAB.QRAB.friend.domain.Friendship;
import QRAB.QRAB.friend.dto.FriendScoreDTO;
import QRAB.QRAB.friend.repository.FriendshipRepository;
import QRAB.QRAB.friend.service.FriendService;
import QRAB.QRAB.note.dto.RecentNoteDTO;
import QRAB.QRAB.note.service.NoteService;
import QRAB.QRAB.quiz.dto.RecentWrongQuizDTO;
import QRAB.QRAB.quiz.dto.UnsolvedQuizSetResponseDTO;
import QRAB.QRAB.quiz.dto.UnsolvedRecentQuizSetDTO;
import QRAB.QRAB.quiz.service.QuizService;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/qrab")
@RestController
@RequiredArgsConstructor
public class HomeController {
    private final NoteService noteService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final QuizService quizService;

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<?> qrabPage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));
        // 최근 노트 3개 조회
        List<RecentNoteDTO> threeNoteInfo = noteService.getUserRecentNotesBy3(username);
        int consecutiveLearningDays = userService.getConsecutiveLearningDays(user); //연속 학습 일수
        int totalLearningDays = userService.getTotalLearningDays(user);//이번달 총 학습일수
        int constellations = totalLearningDays / 3; //별자리개수
        //List<RecentWrongQuizDTO> recentWrongQuizzes = quizService.getRecentWrongQuizzes(username); // 틀린 퀴즈 조회
        List<UnsolvedRecentQuizSetDTO> unsolvedQuizSetResponseDTOS = quizService.getRecentUnsolvedQuizSets(username); //안푼 퀴즈 세트의 노트 최근 3개 조회

        //친구

        List<FriendScoreDTO> friendScoreDTOS = friendService.getFriendScores(user);
        Map<String, Object> result = new HashMap<>();
        result.put("threeNoteInfo", threeNoteInfo);
        result.put("consecutiveLearningDays", consecutiveLearningDays);
        result.put("totalLearningDays", totalLearningDays);
        result.put("constellations", constellations);
        //result.put("recentWrongQuizzes", recentWrongQuizzes);
        result.put("unsolvedQuizSetResponseDTOS", unsolvedQuizSetResponseDTOS);
        result.put("friendScoreDTOS", friendScoreDTOS);

        return ResponseEntity.ok(result);
    }
}
