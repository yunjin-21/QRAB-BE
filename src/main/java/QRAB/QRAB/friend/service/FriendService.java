package QRAB.QRAB.friend.service;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.category.dto.CategoryChildResponseDTO;
import QRAB.QRAB.category.dto.CategoryParentResponseDTO;
import QRAB.QRAB.category.repository.CategoryRepository;
import QRAB.QRAB.category.service.CategoryService;
import QRAB.QRAB.email.domain.Email;
import QRAB.QRAB.email.repository.EmailRepository;
import QRAB.QRAB.friend.domain.Friendship;
import QRAB.QRAB.friend.dto.AddFriendNoteRequestDTO;
import QRAB.QRAB.friend.dto.FriendAddRequestDTO;
import QRAB.QRAB.friend.dto.FriendResponseDTO;
import QRAB.QRAB.friend.dto.FriendScoreDTO;
import QRAB.QRAB.friend.repository.FriendshipRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.FriendNoteResponseDTO;
import QRAB.QRAB.note.dto.NoteResponseDTO;
import QRAB.QRAB.note.repository.NoteRepository;
import QRAB.QRAB.note.service.NoteService;
import QRAB.QRAB.profile.domain.Profile;
import QRAB.QRAB.profile.dto.ProfileResponseDTO;
import QRAB.QRAB.profile.repository.ProfileRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.excepiton.NotFoundMemberException;
import QRAB.QRAB.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service //db접근을 위한 repository와 사용자 인터페이스를 처리하는 controller 사이의 중간 계층 역할
@Transactional(readOnly = true)//데이터를 조회만 하기 - 읽기전용
@RequiredArgsConstructor// final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class FriendService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendRepository;
    private final ProfileRepository profileRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final NoteService noteService;
    private final FriendshipRepository friendshipRepository;
    private final NoteRepository noteRepository;

    private final EmailRepository emailRepository;

    @Transactional(readOnly = false)
    public ResponseEntity<?> saveFriend(FriendAddRequestDTO friendAddRequestDTO){
        User user = userRepository.findOneWithAuthoritiesByUsername(friendAddRequestDTO.getEmail())//user 객체
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + friendAddRequestDTO.getEmail()));
        User friend = userRepository.findByNickname(friendAddRequestDTO.getFriendNickname()) //friend 객체
                .orElseThrow(() -> new NotFoundMemberException("Could not find user's friend in user table: " + friendAddRequestDTO.getFriendNickname()));

        if(user.equals(friend)){
            throw new RuntimeException("Could not add yourself as a friend");
        }
        if(friendRepository.existsByUserAndFriend(user, friend)){
            throw new RuntimeException("Already friends");
        }
        Friendship friendship = friendAddRequestDTO.toEntity(user);//user 정보를 활용
        //friendship.setUser(user); //친구 추가시킨 사용자
        friendship.setFriend(friend); //추가된 친구의 아이디가 추가

        Friendship savedFriendship = friendRepository.save(friendship);
        return ResponseEntity.ok(friendAddRequestDTO);
    }

    @Transactional(readOnly = false)
    public void deleteFriend(Long friendshipId, String userEmail){
        User user = userRepository.findOneWithAuthoritiesByUsername(userEmail)//user 객체
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + userEmail));
        Friendship friendship = friendRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Could not find friendship ID"));

        if(friendship.getUser().equals(user)){
            friendRepository.delete(friendship);
        }else{
            throw new RuntimeException("Different users to delete");
        }
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> getFriendsWithProfile() {
        //현재 로그인된 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User user = userRepository.findByUsername(currentUserEmail)
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + currentUserEmail));
        Optional<Email> email = emailRepository.findTopByToEmailOrderByCreatedAtDesc(user.getUsername());
        // 프로필 목록을 반환 : 닉네임 전화번호 이미지 전공 // 알림 큐랩 스코어
        ProfileResponseDTO userProfileResponse = ProfileResponseDTO.fromEntity(user, email);

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", userProfileResponse.getNickname());
        result.put("phoneNumber", userProfileResponse.getPhoneNumber());
        result.put("imgUrl", userProfileResponse.getImgUrl());
        result.put("majorIds", userProfileResponse.getMajorNames());
        result.put("hour", userProfileResponse.getHour());
        result.put("minute", userProfileResponse.getMinute());
        result.put("ampm", userProfileResponse.getAmpm());


        //친구 목록을 반환
        // firendshipId 아이디 + 닉네임 + 프로필 사진 추가하기
        List<Friendship> friendships = friendRepository.findByUser(user);
        List<FriendResponseDTO> friendResponseDTOs = friendships.stream()
                        .map(FriendResponseDTO::fromEntity)
                        .collect(Collectors.toList());

        result.put("friendships", friendResponseDTOs);

        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> getNotesByFriend(String userEmail, Long friendshipId, int page){
        User user = userRepository.findOneWithAuthoritiesByUsername(userEmail)//user 객체
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + userEmail));
        Friendship friendship = friendRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Could not find friendship ID"));

        String friendEmail = "";
        if(friendship.getUser().equals(user)){
            friendEmail = friendship.getFriend().getUsername();
        }
        // 친구가 생성한 상위 카테고리 조회 - 단 친구의 노트가 0인 경우의 카테고리만 필터링해서 제공
        List<CategoryParentResponseDTO> parentCategories = categoryService.getFriendParentCategories(friendEmail);

        // 하위 카테고리 조회
        List<CategoryChildResponseDTO> childCategories = categoryService.getFriendChildCategories(friendEmail);

        // 노트의 제목, 요약본 (10자), 카테고리 조회
        List<FriendNoteResponseDTO> sixFriendsNotesInfo = noteService.getFriendNotes(friendEmail, page);

        Map<String, Object> result = new HashMap<>();
        result.put("parentCategories", parentCategories);
        result.put("childCategories", childCategories);
        result.put("sixFriendsNotesInfo", sixFriendsNotesInfo);

        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> getFriendNotePageByCategory(String userEmail, Long friendshipId, Long categoryId, int page){
        User user = userRepository.findOneWithAuthoritiesByUsername(userEmail)//user 객체
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + userEmail));
        Friendship friendship = friendRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Could not find friendship ID"));

        String friendEmail = "";
        if(friendship.getUser().equals(user)){
            friendEmail = friendship.getFriend().getUsername();
        }
        // 친구가 생성한 상위 카테고리 조회 - 단 친구의 노트가 public 값이 0인 경우의 카테고리만 필터링해서 제공
        List<CategoryParentResponseDTO> parentCategories = categoryService.getFriendParentCategories(friendEmail);

        // 하위 카테고리 조회
        List<CategoryChildResponseDTO> childCategories = categoryService.getFriendChildCategories(friendEmail);

        // 노트의 제목, 카테고리 조회
        List<FriendNoteResponseDTO> sixFriendsNotesInfo = noteService.getFriendNotesByCategory(friendEmail,categoryId,page);

        Map<String, Object> result = new HashMap<>();
        result.put("parentCategories", parentCategories);
        result.put("childCategories", childCategories);
        result.put("sixFriendsNotesInfo", sixFriendsNotesInfo);

        return ResponseEntity.ok(result);
    }
    @Transactional(readOnly = false)
    public ResponseEntity<?> saveFriendNote(Long noteId, AddFriendNoteRequestDTO addFriendNoteRequestDTO){
        System.out.println("Received noteId: " + noteId);
        System.out.println("Received categoryId: " + addFriendNoteRequestDTO.getCategoryId());
        Note friendNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Could not find noteId"));
        User user = userRepository.findOneWithAuthoritiesByUsername(addFriendNoteRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException(("Could not find user with email")));
        Category category = categoryRepository.findById(addFriendNoteRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Could not find category"));

        Note userNote = addFriendNoteRequestDTO.toEntity(user, category);
        userNote.setRestrictedAccess(0);
        userNote.setUrl(friendNote.getUrl());
        userNote.setFile(friendNote.getFile());
        userNote.setContent(friendNote.getContent());
        userNote.setTitle(friendNote.getTitle());
        userNote.setChatgptContent(friendNote.getChatgptContent());

        // 새로운 노트를 데이터베이스에 저장 후 DTO로 변환하여 반환
        noteRepository.save(userNote);
        return ResponseEntity.ok(addFriendNoteRequestDTO);
    }

    @Transactional(readOnly = true)
    public List<FriendScoreDTO> getFriendScores(User user){

        List<Friendship> friendships = friendshipRepository.findByUser(user);
        String userNickname = user.getNickname();

        // Friendship을 FriendScoreDTO로 변환 (userNickname 추가)
        List<FriendScoreDTO> friendScoreDTOS = friendships.stream()
                .map(friendship -> FriendScoreDTO.fromEntity(friendship, userNickname))  // 수정된 부분
                .collect(Collectors.toList());
        return friendScoreDTOS;
    }
}
