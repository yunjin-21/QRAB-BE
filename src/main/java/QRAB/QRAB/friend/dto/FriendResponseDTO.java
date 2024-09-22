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
    private Long userId;
    private String nickName;
    private List<Long> noteIds; //친구가 작성한 노트 ID 리스트

    public static FriendResponseDTO fromEntity(User friend, List<Note> notes){
        List<Long> noteIds = notes.stream()
                .map(Note::getId)
                .collect(Collectors.toList());

        return new FriendResponseDTO(friend.getUserId(), friend.getNickname(), noteIds);
    }

}
