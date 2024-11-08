package QRAB.QRAB.profile.service;

import QRAB.QRAB.major.domain.Major;
import QRAB.QRAB.major.repository.MajorRepository;
import QRAB.QRAB.note.config.S3Config;
import QRAB.QRAB.profile.domain.Profile;
import QRAB.QRAB.profile.dto.MajorUpdateDTO;
import QRAB.QRAB.profile.dto.NotificationRequestDTO;
import QRAB.QRAB.profile.dto.NotificationResponseDTO;
import QRAB.QRAB.profile.dto.ProfileUpdateDTO;
import QRAB.QRAB.profile.repository.ProfileRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.excepiton.NotFoundMemberException;
import QRAB.QRAB.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public final MajorRepository majorRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Config s3Config;
    @Transactional(readOnly = false)
    public ResponseEntity<?> updateProfile(ProfileUpdateDTO profileUpdateDTO) throws IOException {
        User user = userRepository.findOneWithAuthoritiesByUsername(profileUpdateDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + profileUpdateDTO.getEmail()));

        Profile profile = user.getProfile();

        if (profileUpdateDTO.getImgUrl() != null && !profileUpdateDTO.getImgUrl().isEmpty()) {
            String imgUrl = s3Config.upload(profileUpdateDTO.getImgUrl()); // 새 이미지 업로드 및 URL 가져오기
            profile.setImgUrl(imgUrl);
        }

        if (profileUpdateDTO.getNickName() != null) {
            profile.setNickname(profileUpdateDTO.getNickName());
            user.setNickname(profileUpdateDTO.getNickName());
        }

        if (profileUpdateDTO.getPassword() != null && !profileUpdateDTO.getPassword().isEmpty()) {
            profile.setPassword(profileUpdateDTO.getPassword());
            user.setPassword(passwordEncoder.encode(profileUpdateDTO.getPassword())); // 비밀번호 암호화
        }

        if (profileUpdateDTO.getPhoneNumber() != null) {
            profile.setPhoneNumber(profileUpdateDTO.getPhoneNumber());
            user.setPhoneNumber(profileUpdateDTO.getPhoneNumber());
        }

        profileRepository.save(profile);
        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> updateMajor(MajorUpdateDTO majorUpdateDTO){
        User user = userRepository.findOneWithAuthoritiesByUsername(majorUpdateDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + majorUpdateDTO.getEmail()));
        List<Major> majorList = majorRepository.findAllByNameIn(new ArrayList<>(majorUpdateDTO.getMajorNames()));
        Set<Major> majorSet = new HashSet<>(majorList);

        user.setMajors(majorSet);
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(majorUpdateDTO);
    }


    @Transactional(readOnly = false)
    public ResponseEntity<?> viewNotifications(NotificationRequestDTO notificationRequestDTO){
        User user = userRepository.findOneWithAuthoritiesByUsername(notificationRequestDTO.getEmail())
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));
        if(user.getNotification() == 0){//비설정일 경우
            user.setNotification(1);
        }else{
            user.setNotification(0);
        }
        User savedUser = userRepository.save(user);
        NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO(savedUser.getNotification());
        return ResponseEntity.ok(notificationResponseDTO);
    }
}