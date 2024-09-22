package QRAB.QRAB.friend.dto;


import QRAB.QRAB.friend.domain.Friendship;
import QRAB.QRAB.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendAddRequestDTO {
    private String email;
    private String friendNickname;

    public Friendship toEntity(User user){
        Friendship friendship = Friendship.builder()
                .user(user)
                .friendNickname(friendNickname)
                .build();
        return friendship;
    }
}
