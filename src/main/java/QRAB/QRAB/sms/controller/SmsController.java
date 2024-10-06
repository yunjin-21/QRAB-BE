/*package QRAB.QRAB.sms.controller;

import QRAB.QRAB.sms.dto.SmsRequestDTO;
import QRAB.QRAB.sms.service.SMSService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/sms")
@RestController //http 요청을 처리하고 json 형식으로 데이터를 반환 - restful web service  @controller 와 @responseBody 를 함께 사용
@RequiredArgsConstructor // final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class SmsController {
    private final SMSService smsService;
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendSms(@RequestBody SmsRequestDTO smsRequestDTO ) {
            smsService.sendSms(smsRequestDTO.getPhoneNumber(), smsRequestDTO.getMessage());
            return ResponseEntity.ok("Successfully Sent SMS");

    }
}
*/