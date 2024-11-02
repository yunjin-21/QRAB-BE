package QRAB.QRAB.user.controller;


import QRAB.QRAB.user.dto.UserDTO;
import QRAB.QRAB.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam("email") String email){
        boolean isDuplicate = userService.checkEmailDuplicate(email);
        if (isDuplicate) {
            return ResponseEntity.ok("이미 사용 중인 이메일입니다."); // 이메일이 이미 사용 중일 때
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다."); // 이메일을 사용할 수 있을 때
        }
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNicknameDuplicate(@RequestParam("nickname") String nickname){
        boolean isDuplicate = userService.checkNicknameDuplicate(nickname);
        if (isDuplicate) {
            return ResponseEntity.ok("이미 사용 중인 닉네임입니다."); // 닉네임이 이미 사용 중일 때
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임입니다."); // 닉네임을 사용할 수 있을 때
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody UserDTO userDto
    ) {
        // 학과 검증
        if (userDto.getMajorNames() == null || userDto.getMajorNames().isEmpty()) {
            return ResponseEntity.badRequest().body("학과는 적어도 1개 이상 선택해야 합니다.");// 학과를 하나도 선택하지 않은 경우
        }
        if (userDto.getMajorNames().size() > 3) {
            return ResponseEntity.badRequest().body("학과는 최대 3개까지 선택할 수 있습니다.");// 학과를 3개 초과 선택한 경우
        }
        return ResponseEntity.ok(userService.signup(userDto));
    }

    @GetMapping("/liberal-arts")
    public ResponseEntity<?> getLiberalArts(){
        return userService.getLiberalArts();
    }
    @GetMapping("/natural-sciences")
    public ResponseEntity<?> getNaturalSciences(){
        return userService.getNaturalSciences();
    }
    @GetMapping("/entertainment-sports")
    public ResponseEntity<?> getEntertainmentSports(){
        return userService.getEntertainmentSports();
    }



}