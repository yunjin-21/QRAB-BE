package QRAB.QRAB.friend.controller;

import QRAB.QRAB.friend.dto.AddFriendNoteRequestDTO;
import QRAB.QRAB.friend.dto.FriendAddRequestDTO;
import QRAB.QRAB.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/friends")
@RestController //http 요청을 처리하고 json 형식으로 데이터를 반환 - restful web service  @controller와 @responseBody를 함께 사용
@RequiredArgsConstructor // final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class FriendController {

    private final FriendService friendService;
    @PostMapping
    public ResponseEntity<?> addFriend(@RequestBody FriendAddRequestDTO friendAddRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        friendAddRequestDTO.setEmail(authentication.getName());
        return friendService.saveFriend(friendAddRequestDTO);
    }

    @DeleteMapping("{friendshipId}")
    public ResponseEntity<?> deleteFriend(@PathVariable("friendshipId") Long friendshipId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            friendService.deleteFriend(friendshipId, authentication.getName());
            return ResponseEntity.ok("Friendship is deleted successfully");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //마이페이지에서 친구 눌렀을때 친구의 노트들 모두 조회
    @GetMapping("/{friendshipId}/notes")
    public ResponseEntity<?> getFriendNotes(@PathVariable("friendshipId")Long friendshipId, @RequestParam(name = "page", defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return friendService.getNotesByFriend(userEmail, friendshipId, page);
    }

    @PostMapping("/notes/{noteId}")
    public ResponseEntity<?> saveFriendNote(@PathVariable("noteId") Long noteId, AddFriendNoteRequestDTO addFriendNoteRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        addFriendNoteRequestDTO.setEmail(authentication.getName());
        return friendService.saveFriendNote(noteId, addFriendNoteRequestDTO);
    }

    @GetMapping("/{friendshipId}/notes/{categoryId}")
    public ResponseEntity<?> getFriendNotePageByCategory(@PathVariable("friendshipId")Long friendshipId, @PathVariable("categoryId") Long categoryId, @RequestParam(name = "page", defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return friendService.getFriendNotePageByCategory(userEmail, friendshipId, categoryId, page);
    }


}
