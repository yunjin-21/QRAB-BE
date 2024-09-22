package QRAB.QRAB.profile.service;

import QRAB.QRAB.note.config.S3Config;
import QRAB.QRAB.profile.domain.Profile;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Config s3Config;
    @Transactional(readOnly = false)
    public ResponseEntity<?> updateProfile(ProfileUpdateDTO profileUpdateDTO) throws IOException {
        User user = userRepository.findOneWithAuthoritiesByUsername(profileUpdateDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + profileUpdateDTO.getEmail()));

        Profile profile = user.getProfile();
        if(profile == null){
            profile = Profile.builder().
                    user(user)
                    .build();
            user.setProfile(profile);
        }

        if (profileUpdateDTO.getImgUrl() != null && !profileUpdateDTO.getImgUrl().isEmpty()) {
            String imgUrl = s3Config.upload(profileUpdateDTO.getImgUrl()); // 새 이미지 업로드 및 URL 가져오기
            profile.setImgUrl(imgUrl);
        }

        if (profileUpdateDTO.getNickName() != null) {
            profile.setNickname(profileUpdateDTO.getNickName());
            user.setNickname(profileUpdateDTO.getNickName());
        }else{
            profile.setNickname(user.getNickname());
        }

        if (profileUpdateDTO.getPassword() != null && !profileUpdateDTO.getPassword().isEmpty()) {
            profile.setPassword(profileUpdateDTO.getPassword());
            user.setPassword(passwordEncoder.encode(profileUpdateDTO.getPassword())); // 비밀번호 암호화
        }else{
            profile.setPassword(user.getPassword());
        }

        if (profileUpdateDTO.getPhoneNumber() != null) {
            profile.setPhoneNumber(profileUpdateDTO.getPhoneNumber());
            user.setPhoneNumber(profileUpdateDTO.getPhoneNumber());
        }else {
            profile.setPhoneNumber(user.getPhoneNumber());
        }
        profileRepository.save(profile);
        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }
}
