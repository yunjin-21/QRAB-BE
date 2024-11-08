package QRAB.QRAB.profile.dto;

import QRAB.QRAB.email.domain.Email;
import QRAB.QRAB.major.domain.Major;
import QRAB.QRAB.profile.domain.Profile;
import QRAB.QRAB.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProfileResponseDTO {
    //닉네임 전화번호 이미지 전공 //알림 큐랩 스코어
    private String nickname;
    private String phoneNumber;
    private String imgUrl;
    private List<String> majorNames;
    private int hour;
    private int minute;
    private String ampm;

    public static ProfileResponseDTO fromEntity(User user, Optional<Email> email){
        Profile profile = user.getProfile();
        List<String> majorNames = user.getMajors().stream()
                .map(Major::getName)
                .collect(Collectors.toList());

        int hour = email.map(Email::getHour).orElse(0); // 기본값 0시
        int minute = email.map(Email::getMinute).orElse(0); // 기본값 0분
        String ampm = email.map(Email::getAmpm).orElse("AM"); // 기본값 AM

        return new ProfileResponseDTO(
                        profile.getNickname(),
                        profile.getPhoneNumber(),
                        profile.getImgUrl(),
                        majorNames,
                        hour,
                        minute,
                        ampm
                );
    }

}
