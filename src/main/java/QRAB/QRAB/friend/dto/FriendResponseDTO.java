package QRAB.QRAB.friend.dto;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.friend.domain.Friendship;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class FriendResponseDTO {
    private Long friendshipId;
    private String nickName;
    //프로필 사진 추가하기

    //private List<Long> noteIds; //친구가 작성한 노트 ID 리스트

    public static FriendResponseDTO fromEntity(Friendship friendship){
        User friend = friendship.getFriend(); //친구 객체 가져오기
        return new FriendResponseDTO(friendship.getFriendshipId(), friend.getNickname());
    }

}
