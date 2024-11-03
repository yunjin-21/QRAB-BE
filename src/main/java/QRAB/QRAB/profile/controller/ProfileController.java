package QRAB.QRAB.profile.controller;

import QRAB.QRAB.friend.service.FriendService;
import QRAB.QRAB.profile.dto.MajorUpdateDTO;
import QRAB.QRAB.profile.dto.NotificationRequestDTO;
import QRAB.QRAB.profile.dto.ProfileUpdateDTO;
import QRAB.QRAB.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/profiles")
@RestController //http 요청을 처리하고 json 형식으로 데이터를 반환 - restful web service  @controller와 @responseBody를 함께 사용
@RequiredArgsConstructor // final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class ProfileController {
    private final ProfileService profileService;
    private final FriendService friendService;

    @GetMapping //친구리스트 + 프로필 페이지
    public ResponseEntity<?> getFriends(){
        return friendService.getFriendsWithProfile();
    }

    //프로필 편집
    @PatchMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(ProfileUpdateDTO profileUpdateDTO) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        profileUpdateDTO.setEmail(authentication.getName());
        return profileService.updateProfile(profileUpdateDTO);

    }

    @PutMapping("/updateMajor")
    public ResponseEntity<?> updateMajor(@RequestBody MajorUpdateDTO majorUpdateDTO) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        majorUpdateDTO.setEmail(authentication.getName());
        return profileService.updateMajor(majorUpdateDTO);

    }

    @PostMapping("/notifications")
    public ResponseEntity<?> viewNotifications(NotificationRequestDTO notificationRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        notificationRequestDTO.setEmail(authentication.getName());
        return profileService.viewNotifications(notificationRequestDTO);
    }

}
