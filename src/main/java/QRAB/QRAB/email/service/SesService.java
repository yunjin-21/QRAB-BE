package QRAB.QRAB.email.service;


import QRAB.QRAB.email.domain.Email;
import QRAB.QRAB.email.dto.SesRequestDTO;
import QRAB.QRAB.email.dto.SesResponseDTO;
import QRAB.QRAB.email.repository.EmailRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.excepiton.NotFoundMemberException;
import QRAB.QRAB.user.repository.UserRepository;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.*;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class SesService {

    private final AmazonSimpleEmailService sesClient;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    private final TaskScheduler taskScheduler;

    public void sendEmailAtScheduledTime(SesRequestDTO sesRequestDTO){
        User user = userRepository.findOneWithAuthoritiesByUsername(sesRequestDTO.getTo())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + sesRequestDTO.getTo()));

        if(user.getNotification() == 0){ //비설정
            System.out.println("Notifications are disabled for this user:" + sesRequestDTO.getTo());
            return;
        }
        String nickName = user.getNickname();
        String title = "님! Qrab에서 퀴즈도 풀고 이번달 별자리도 채워보시는건 어떨까요?";
        sesRequestDTO.setSubject(nickName + title);
        sesRequestDTO.setBodyText("위 링크를 클릭하면 Qrab 서비스로 이동합니다.");

        LocalDateTime now = LocalDateTime.now();
        int hour = sesRequestDTO.getHour();
        int minute = sesRequestDTO.getMinute();

        // AM/PM 처리
        if ("PM".equalsIgnoreCase(sesRequestDTO.getAmpm()) && hour < 12) {
            hour += 12;  // PM인 경우 12시 추가
        } else if ("AM".equalsIgnoreCase(sesRequestDTO.getAmpm()) && hour == 12) {
            hour = 0;  // AM 12시는 자정(00:00)
        }
        // 예약 시간 설정
        LocalDateTime scheduledTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                hour, minute, 0, 0);
         //9시간 추가로 시간대 보정
        //LocalDateTime scheduledTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hour, minute)
          //      .plusHours(9);
        // 예약 시간이 현재 시간보다 과거인 경우 다음 날로 설정
        if (scheduledTime.isBefore(now)) {
            scheduledTime = scheduledTime.plusDays(1);
        }
        System.out.println(hour + " " + minute + " Scheduling email for: " + sesRequestDTO.getTo() + " at " + scheduledTime);

        // LocalDateTime을 ZonedDateTime으로 변환 후 Instant로 변환
        ZonedDateTime zonedDateTime = scheduledTime.atZone(ZoneId.of("Asia/Seoul"));// KST로 변환
        Instant instant = zonedDateTime.toInstant();
        System.out.println("예약된 시간 (KST): " + zonedDateTime);
        System.out.println("예약된 시간 (Instant): " + instant);
        System.out.println("서버의 시간대: " + ZoneId.systemDefault());
        System.out.println("현재 시간 (KST): " + ZonedDateTime.now(ZoneId.of("Asia/Seoul")));

        System.out.println("Scheduled email for: " + sesRequestDTO.getTo() + " at " + zonedDateTime);
        // Email 엔티티 생성 후 저장
        Email email = Email.builder()
                .toEmail(sesRequestDTO.getTo())
                .hour(sesRequestDTO.getHour())
                .minute(sesRequestDTO.getMinute())
                .ampm(sesRequestDTO.getAmpm())
                .scheduledTime(zonedDateTime)
                .build();

        emailRepository.save(email);

        //System.out.println(zonedDateTime);
        // 예약된 시간에 이메일 발송
        taskScheduler.schedule(() -> sendEmail(sesRequestDTO), instant);
    }
    public void sendEmail(SesRequestDTO sesRequestDTO) {
        Context context = new Context();
        context.setVariable("bodyText", sesRequestDTO.getBodyText());
        String htmlBody = templateEngine.process("emailTemplate", context);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(sesRequestDTO.getTo()))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content().withCharset("UTF-8").withData(htmlBody))
                                .withText(new Content().withCharset("UTF-8").withData(sesRequestDTO.getBodyText())))
                        .withSubject(new Content().withCharset("UTF-8").withData(sesRequestDTO.getSubject())))
                        .withSource(sesRequestDTO.getFrom());
        
        String messageId = sesClient.sendEmail(request).getMessageId();
        System.out.println("Email sent with MessageId: " + messageId);
    }

}
