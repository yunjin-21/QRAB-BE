package QRAB.QRAB.friend.service;

import QRAB.QRAB.category.dto.CategoryChildResponseDTO;
import QRAB.QRAB.category.dto.CategoryParentResponseDTO;
import QRAB.QRAB.category.service.CategoryService;
import QRAB.QRAB.friend.domain.Friendship;
import QRAB.QRAB.friend.dto.FriendAddRequestDTO;
import QRAB.QRAB.friend.dto.FriendResponseDTO;
import QRAB.QRAB.friend.repository.FriendshipRepository;
import QRAB.QRAB.note.domain.Note;
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
    private final CategoryService categoryService;
    private final NoteService noteService;

    private final NoteRepository noteRepository;
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
        Friendship friendship = friendAddRequestDTO.toEntity(user);
        //friendship.setUser(user); //친구 추가시킨 사용자
        friendship.setFriend(friend); //추가된 친구

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

        // 프로필 목록을 반환 : 닉네임 전화번호 이미지 전공 // 알림 큐랩 스코어
        ProfileResponseDTO userProfileResponse = ProfileResponseDTO.fromEntity(user);
        Map<String, Object> result = new HashMap<>();
        result.put("nickname", userProfileResponse.getNickname());
        result.put("phoneNumber", userProfileResponse.getPhoneNumber());
        result.put("imgUrl", userProfileResponse.getImgUrl());
        result.put("majorIds", userProfileResponse.getMajorNames());


        //친구 목록을 반환 아이디 + 닉네임 + 노트 아이디
        List<Friendship> friendships = user.getFriends();
        List<FriendResponseDTO> friendResponseDTOs = friendships.stream()
                .map(friendship -> {
                    User friend = friendship.getFriend(); // 친구 객체 찾기
                    if (friend == null) {
                        // 친구가 null인 경우 기본값 또는 예외 처리
                        return null;
                    }
                    List<Note> notes = noteRepository.findByUser(friend); // 친구가 만든 노트 리스트 찾기
                    return FriendResponseDTO.fromEntity(friend, notes);
                })
                .filter(Objects::nonNull) // null 필터링
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
        // 사용자가 생성한 상위 카테고리 조회
        List<CategoryParentResponseDTO> parentCategories = categoryService.getUserParentCategories(friendEmail);

        // 하위 카테고리 조회
        List<CategoryChildResponseDTO> childCategories = categoryService.getUserChildCategories(friendEmail);

        // 노트의 제목, 요약본 (10자), 카테고리 조회
        List<NoteResponseDTO> sixNotesInfo = noteService.getUserRecentNotes(friendEmail, page);

        Map<String, Object> result = new HashMap<>();
        result.put("parentCategories", parentCategories);
        result.put("childCategories", childCategories);
        result.put("sixNotesInfo", sixNotesInfo);

        return ResponseEntity.ok(result);
    }

}
