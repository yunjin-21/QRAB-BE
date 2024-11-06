package QRAB.QRAB.email.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SesRequestDTO{
    private String from;
    private String to; // 보내는 이메일 현재 아이디값!
    private String subject;
    private String bodyText;
    private int hour; // 1~12
    private int minute; //0~59
    private String ampm;
}