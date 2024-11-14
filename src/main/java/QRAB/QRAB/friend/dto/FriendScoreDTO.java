package QRAB.QRAB.friend.dto;

import QRAB.QRAB.friend.domain.Friendship;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendScoreDTO {
    private String friendNickname;
    private String userNickname;  // 추가된 필드

    public static FriendScoreDTO fromEntity(Friendship friendship,String userNickname){
        return new FriendScoreDTO(friendship.getFriend().getNickname(), userNickname);
    }
}
